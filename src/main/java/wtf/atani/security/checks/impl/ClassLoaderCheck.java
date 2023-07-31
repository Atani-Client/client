package wtf.atani.security.checks.impl;

import wtf.atani.security.checks.ProtectionCheck;
import wtf.atani.security.checks.enums.TriggerType;
import wtf.atani.security.checks.manager.ProtectionManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
public final class ClassLoaderCheck extends ProtectionCheck {

    private static final String[] CLASS_NAMES = {
            "sun.instrument.InstrumentationImpl",
            "java.lang.instrument.Instrumentation",
            "java.lang.instrument.ClassDefinition",
            "java.lang.instrument.ClassFileTransformer",
            "java.lang.instrument.IllegalClassFormatException",
            "java.lang.instrument.UnmodifiableClassException"
    };

    private final Method findLoadedClassMethod;

    public ClassLoaderCheck() {
        super(TriggerType.REPETITIVE, true);

        try {
            this.findLoadedClassMethod = ClassLoader.class.getDeclaredMethod("findLoadedClass0", String.class);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean check() throws InvocationTargetException, IllegalAccessException, InterruptedException {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        // Check if any classes related to the java instrumentation API have been loaded.
        // Java agents use the instrumentation API so this check is quite useful.
        for (final String className : CLASS_NAMES) {
            final Object loadedClass = this.findLoadedClassMethod.invoke(classLoader, className);

            if (loadedClass != null) {
                // Try to crash immediately instead of hanging in an infinite loop.
                ProtectionManager.getInstance().crash();
                return true;
            }
        }

        return false;
    }
}
