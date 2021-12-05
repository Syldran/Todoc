package com.cleanup.todoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.cleanup.todoc.database.TodocDatabase;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TaskDaoTest {

    //DATA SET FOR TEST
    public static long PROJECT_ID = 1L;
    public static Project PROJECT_DEMO = new Project(PROJECT_ID, "Lampion", 0xFFEADAD1);
    public static Task NEW_TASK_ALPHA = new Task(1L, PROJECT_ID, "TACHE ALPHA", 10);
    public static Task NEW_TASK_BETA = new Task(2L, PROJECT_ID, "TACHE BETA", 10);
    public static Task NEW_TASK_GAMMA = new Task(3L, PROJECT_ID, "TACHE GAMMA", 10);
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    //public static Task NEW_TASK_ZETA = new Task(3L,PROJECT_ID, "TACHE ZETA", 10);
    private TodocDatabase database;

    @Before
    public void initDb() throws Exception {
        this.database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext(),
                TodocDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() throws Exception {
        database.close();
    }

    @Test
    public void insertAndGetProject() throws InterruptedException {
        // BEFORE : Adding a new user
        this.database.projectDao().createProject(PROJECT_DEMO);
        // TEST
        Project project = LiveDataTestUtil.getValue(this.database.projectDao().getProject(PROJECT_ID));
        assertTrue(project.getName().equals(PROJECT_DEMO.getName()) && project.getId() == PROJECT_ID);
    }

    @Test
    public void getTasksWhenNoTaskInserted() throws InterruptedException {
        // TEST
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getTasks(PROJECT_ID));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void insertAndGetTasks() throws InterruptedException {
        // BEFORE : Adding demo project & demo tasks

        this.database.projectDao().createProject(PROJECT_DEMO);
        this.database.taskDao().insertTask(NEW_TASK_ALPHA);
        this.database.taskDao().insertTask(NEW_TASK_BETA);
        this.database.taskDao().insertTask(NEW_TASK_GAMMA);

        // TEST
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getTasks(PROJECT_ID));
        assertEquals(3, tasks.size());
    }

    @Test
    public void insertAndUpdateTask() throws InterruptedException {
        // BEFORE : Adding demo user & demo items. Next, update item added & re-save it
        this.database.projectDao().createProject(PROJECT_DEMO);
        this.database.taskDao().insertTask(NEW_TASK_GAMMA);
        Task taskAdded = LiveDataTestUtil.getValue(this.database.taskDao().getTasks(PROJECT_ID)).get(0);
        taskAdded.setName("tache GamMa");
        this.database.taskDao().updateTask(taskAdded);

        //TEST
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getTasks(PROJECT_ID));
        assertTrue(tasks.size() == 1 && tasks.get(0).getName().contentEquals("tache GamMa"));
        System.out.println(tasks.get(0).getName());
    }

    @Test
    public void insertAndDeleteTask() throws InterruptedException {
        // BEFORE : Adding demo user & demo item. Next, get the item added & delete it.
        this.database.projectDao().createProject(PROJECT_DEMO);
        this.database.taskDao().insertTask(NEW_TASK_GAMMA);
        Task taskAdded = LiveDataTestUtil.getValue(this.database.taskDao().getTasks(PROJECT_ID)).get(0);
        this.database.taskDao().deleteTask(taskAdded.getId());

        //TEST
        List<Task> tasks = LiveDataTestUtil.getValue(this.database.taskDao().getTasks(PROJECT_ID));
        assertTrue(tasks.isEmpty());
    }
}
