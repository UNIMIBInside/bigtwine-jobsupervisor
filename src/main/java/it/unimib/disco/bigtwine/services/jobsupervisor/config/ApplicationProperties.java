package it.unimib.disco.bigtwine.services.jobsupervisor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Jobsupervisor.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private TwitterNeel twitterNeel = new TwitterNeel();

    public TwitterNeel getTwitterNeel() {
        return twitterNeel;
    }

    public static class TwitterNeel {
        private Stream stream = new Stream();

        public Stream getStream() {
            return stream;
        }

        public static class Stream {
            private String defaultLang = ApplicationDefaults.TwitterNeel.Stream.defaultLang;
            private int sampling = ApplicationDefaults.TwitterNeel.Stream.sampling;
            private FlinkJob flinkJob = new FlinkJob();

            public String getDefaultLang() {
                return defaultLang;
            }

            public void setDefaultLang(String defaultLang) {
                this.defaultLang = defaultLang;
            }

            public int getSampling() {
                return sampling;
            }

            public void setSampling(int sampling) {
                this.sampling = sampling;
            }

            public FlinkJob getFlinkJob() {
                return flinkJob;
            }

            public static class FlinkJob {
                private String jarName = ApplicationDefaults.TwitterNeel.Stream.FlinkJob.jarName;
                private String jarClass = ApplicationDefaults.TwitterNeel.Stream.FlinkJob.jarClass;

                public String getJarName() {
                    return jarName;
                }

                public void setJarName(String jarName) {
                    this.jarName = jarName;
                }

                public String getJarClass() {
                    return jarClass;
                }

                public void setJarClass(String jarClass) {
                    this.jarClass = jarClass;
                }
            }
        }
    }
}
