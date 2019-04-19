package it.unimib.disco.bigtwine.services.jobsupervisor.config;

public interface ApplicationDefaults {
    interface TwitterNeel {
        interface Stream {
            String defaultLang = "en";
            int sampling = -1;

            interface FlinkJob {
                String jarName = "StreamProcessor.jar";
                String jarClass = "it.unimib.disco.bigtwine.streamprocessor.TwitterStreamJob";
            }
        }
    }
}
