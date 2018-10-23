def call(def codeEnv) {       
       stage('Check Availability') {            
              waitUntil {
                  try {         
                      sh "curl -s --head  --request GET  localhost:8081/actuator/health | grep '200'"
                      return true
                  } catch (Exception e) {
                        return false
                  }
           }
       }
}