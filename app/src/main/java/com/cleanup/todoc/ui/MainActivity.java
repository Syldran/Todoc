package com.cleanup.todoc.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cleanup.todoc.R;
import com.cleanup.todoc.databinding.ActivityMainBinding;
import com.cleanup.todoc.injections.Injection;
import com.cleanup.todoc.injections.ViewModelFactory;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>Home activity of the application which is displayed when the user opens the app.</p>
 * <p>Displays the list of tasks.</p>
 *
 * @author Gaëtan HERFRAY
 */
public class MainActivity extends AppCompatActivity implements TasksAdapter.DeleteTaskListener {

    @Nullable
    public AlertDialog dialog = null;
    private ActivityMainBinding binding;
    private ArrayList<Project> allProjects = new ArrayList<Project>();
    @NonNull
    private ArrayList<Task> tasks = new ArrayList<>();
    private final TasksAdapter adapter = new TasksAdapter(tasks, this);
    private TaskViewModel taskViewModel;
    @NonNull
    private SortMethod sortMethod = SortMethod.NONE;
    @Nullable
    private EditText dialogEditText = null;
    @Nullable
    private Spinner dialogSpinner = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configRecyclerView();

        setBtnAddTask();

        configViewModel();
    }

    public void configRecyclerView() {
        binding.listTasks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.listTasks.setAdapter(adapter);
    }

    public void setBtnAddTask() {
        binding.fabAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    public void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        this.taskViewModel = new ViewModelProvider(this, viewModelFactory).get(TaskViewModel.class);
        this.taskViewModel.getAllTasks().observe(this, this::updateTasksList);
        this.taskViewModel.getAllProjects().observe(this, new Observer<List<Project>>() {
            @Override
            public void onChanged(List<Project> projects) {
                allProjects = (ArrayList<Project>) projects;
                adapter.updateProjects(allProjects);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.filter_alphabetical) {
            sortMethod = SortMethod.ALPHABETICAL;
        } else if (id == R.id.filter_alphabetical_inverted) {
            sortMethod = SortMethod.ALPHABETICAL_INVERTED;
        } else if (id == R.id.filter_oldest_first) {
            sortMethod = SortMethod.OLD_FIRST;
        } else if (id == R.id.filter_recent_first) {
            sortMethod = SortMethod.RECENT_FIRST;
        }

        updateTasks();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeleteTask(Task task) {
        taskViewModel.deleteTask(task.getId());
        updateTasks();
    }

    /**
     * Called when the user clicks on the positive button of the Create Task Dialog.
     *
     * @param dialogInterface the current displayed dialog
     */
    public void onPositiveButtonClick(DialogInterface dialogInterface) { //private
        // If dialog is open
        if (dialogEditText != null && dialogSpinner != null) {
            // Get the name of the task
            String taskName = dialogEditText.getText().toString();

            // Get the selected project to be associated to the task
            Project taskProject = null;
            if (dialogSpinner.getSelectedItem() instanceof Project) {
                taskProject = (Project) dialogSpinner.getSelectedItem();
            }

            // If a name has not been set
            if (taskName.trim().isEmpty()) {
                dialogEditText.setError(getString(R.string.empty_task_name));
            }
            // If both project and name of the task have been set
            else if (taskProject != null) {
                // TODO: Replace this by id of persisted task
                long id = (long) (Math.random() * 50000);


                Task task = new Task(
                        //  id,
                        taskProject.getId(),
                        taskName,
                        new Date().getTime()
                );

                addTask(task);

                dialogInterface.dismiss();
            }
            // If name has been set, but project has not been set (this should never occur)
            else {
                dialogInterface.dismiss();
            }
        }
        // If dialog is aloready closed
        else {
            dialogInterface.dismiss();
        }
    }

    /**
     * Shows the Dialog for adding a Task
     */
    public void showAddTaskDialog() {//private ?
        final AlertDialog dialog = getAddTaskDialog();

        dialog.show();

        dialogEditText = dialog.findViewById(R.id.txt_task_name);
        dialogSpinner = dialog.findViewById(R.id.project_spinner);

        populateDialogSpinner();
    }

    /**
     * Adds the given task to the list of created tasks.
     *
     * @param task the task to be added to the list
     */
    private void addTask(@NonNull Task task) {
        taskViewModel.createTask(task);
        updateTasks();
    }


    private void updateTasksList(List<Task> tasks) {
        this.tasks = (ArrayList<Task>) tasks;
        updateTasks();
    }

    /**
     * Updates the list of tasks in the UI
     */
    public void updateTasks() {//private
        if (tasks.size() == 0) {
            binding.lblNoTask.setVisibility(View.VISIBLE);
            binding.listTasks.setVisibility(View.GONE);
        } else {
            binding.lblNoTask.setVisibility(View.GONE);
            binding.listTasks.setVisibility(View.VISIBLE);
            switch (sortMethod) {
                case ALPHABETICAL:
                    Collections.sort(tasks, new Task.TaskAZComparator());
                    break;
                case ALPHABETICAL_INVERTED:
                    Collections.sort(tasks, new Task.TaskZAComparator());
                    break;
                case RECENT_FIRST:
                    Collections.sort(tasks, new Task.TaskRecentComparator());
                    break;
                case OLD_FIRST:
                    Collections.sort(tasks, new Task.TaskOldComparator());
                    break;

            }
            adapter.updateTasks(tasks);
        }
    }

    /**
     * Returns the dialog allowing the user to create a new task.
     *
     * @return the dialog allowing the user to create a new task
     */
    @NonNull
    public AlertDialog getAddTaskDialog() {//private
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);

        alertBuilder.setTitle(R.string.add_task);
        alertBuilder.setView(R.layout.dialog_add_task);
        alertBuilder.setPositiveButton(R.string.add, null);
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogEditText = null;
                dialogSpinner = null;
                dialog = null;
            }
        });

        dialog = alertBuilder.create();

        // This instead of listener to positive button in order to avoid automatic dismiss
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        onPositiveButtonClick(dialog);
                    }
                });
            }
        });

        return dialog;
    }

    /**
     * Sets the data of the Spinner with projects to associate to a new task
     */
    public void populateDialogSpinner() {//private
        final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allProjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (dialogSpinner != null) {
            dialogSpinner.setAdapter(adapter);
        }
        /*taskViewModel.getAllProjects().observe(this, list -> {
            final ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (dialogSpinner != null) {
                dialogSpinner.setAdapter(adapter);
            }
        });*/
    }

    /**
     * List of all possible sort methods for task
     */
    public enum SortMethod {//private
        /**
         * Sort alphabetical by name
         */
        ALPHABETICAL,
        /**
         * Inverted sort alphabetical by name
         */
        ALPHABETICAL_INVERTED,
        /**
         * Lastly created first
         */
        RECENT_FIRST,
        /**
         * First created first
         */
        OLD_FIRST,
        /**
         * No sort
         */
        NONE
    }
}
