def call(def codeEnv) {       
       stage('Check Supervision') {
			def props = readProperties interpolate: false, file: "${codeEnv}/.env"
			def urlEnv=props.URL;
			def urlCheck="${urlEnv}/status"
              waitUntil {
                  try {         
                      sh "curl -s --head  --request GET  ${urlCheck} | grep 'ok\|OK'"
                      return true
                  } catch (Exception e) {
                      return false
                  }
           }
       }
	}