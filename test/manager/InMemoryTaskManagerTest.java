package manager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {


    @Override
    InMemoryTaskManager getTaskManager() {
        return new InMemoryTaskManager();
    }
}

