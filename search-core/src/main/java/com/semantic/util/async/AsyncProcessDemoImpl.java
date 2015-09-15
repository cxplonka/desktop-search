package com.semantic.util.async;

public class AsyncProcessDemoImpl implements AsyncProcessDemoInterface {

    @AsynchProcess(onSuccess = "onAsyncSuccess", onFailure = "onAsyncFailure")
    public String legacyProcess(final String name, final Long tm) {
        try {
            Thread.currentThread().sleep(tm); // emulate some proc time
        } catch (final InterruptedException e) {
        }
        if (name.equals("force-error")) {
            throw new RuntimeException("emulating test exception here");
        }
        return "##legacyProcess returning: " + name + " tm.ms:" + tm + " " + System.currentTimeMillis();
    }

    public <T> void onAsyncSuccess(final T result) {
        System.out.println(">>onAsyncSuccess : " + result);
    }

    public void onAsyncFailure(final Throwable t) {
        System.err.println(">>onAsyncFailure : " + t.toString());
    }

    public static void main(final String[] args) {

        final AsyncProcessDemoInterface targetA =
                AsyncProcessEngine.getInstance().createAsyncProxyProcess(new AsyncProcessDemoImpl(), AsyncProcessDemoInterface.class);

        final AsyncProcessDemoInterface targetB =
                AsyncProcessEngine.getInstance().createAsyncProxyProcess(new AsyncProcessDemoImpl(), AsyncProcessDemoInterface.class);        
        
        for (int i = 1; i < 6; i++) {
            targetA.legacyProcess("A-asynced-" + i, (2000L * i));
            targetB.legacyProcess("B-asynced-" + i, (1000L * i));
            targetB.legacyProcess("force-error", 0L);
        }

        try {
            Thread.currentThread().sleep(15000);
            AsyncProcessEngine.getInstance().shutdown();
            System.out.println("SHUTDOWN");
        } catch (final InterruptedException e) {
        }
    }
}
