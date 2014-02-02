(defproject liberator-demo "0.1.0-SNAPSHOT"
  :description "Liberator demo"
  :url "https://github.com/AustinClojure/liberator-demo"

  :dependencies [[ring-server "0.3.1"]
                 [com.h2database/h2 "1.3.175"]
                 [domina "1.0.2"]
                 [com.taoensso/timbre "3.0.0"]
                 [environ "0.4.0"]
                 [markdown-clj "0.9.41"]
                 [prismatic/dommy "0.1.2"]
                 [korma "0.3.0-RC6"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [com.taoensso/tower "2.0.1"]
                 [org.clojure/clojure "1.5.1"]
                 [log4j "1.2.17" :exclusions [javax.mail/mail javax.jms/jms com.sun.jdmk/jmxtools com.sun.jmx/jmxri]]
                 [cljs-ajax "0.2.3"]
                 [lib-noir "0.8.0"]
                 [compojure "1.1.6"]
                 [selmer "0.5.9"]]

  :plugins [[lein-ring "0.8.10"]
            [lein-environ "0.4.0"]
            [lein-cljsbuild "0.3.3"]]

  :profiles {:uberjar {:aot :all}
             :production {:ring {:open-browser? false
                                 :stacktraces? false
                                 :auto-reload? false}}
             :dev {:dependencies [[ring-mock "0.1.5"]
                                  [ring/ring-devel "1.2.1"]]
                   :env {:dev true}}}

  :cljsbuild {:builds [{:source-paths ["src-cljs"]
                        :compiler {:pretty-print false
                                   :output-to "resources/public/js/site.js"
                                   :optimizations :advanced}}]}

  :ring {:handler liberator-demo.handler/app
         :init liberator-demo.handler/init
         :destroy liberator-demo.handler/destroy
         :nrepl {:start? true}}

  :repl-options {:init-ns liberator-demo.repl})
