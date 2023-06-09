package fr.boul2gom.cerberus.api;

public class References {

    public static final String CONFIGURATION_PATH = "config.yml";
    public static final String LANGUAGE_PATH = "lang";

    public static class Provider {

        private static CerberusAPI INSTANCE;

        public static <I extends CerberusAPI> void register(I instance) {
            if (Provider.INSTANCE != null) {
                throw new IllegalStateException("Failed to register CerberusAPI instance. May another instance is registered ?");
            }

            Provider.INSTANCE = instance;
        }

        public static CerberusAPI get() {
            if (Provider.INSTANCE == null) {
                throw new IllegalStateException("Failed to get CerberusAPI instance. Make sure an instance is registered.");
            }

            return Provider.INSTANCE;
        }
    }
}
