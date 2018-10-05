def call(Closure context) {
    // création d'un map
    def params = [:]
    // affecter la map en tant que delegate de la closure
    context.resolveStrategy = Closure.DELEGATE_FIRST
    context.delegate = params
    // appeler la closure
    context()
	def dockerRegistryRepoAppli
	def dockerRegistryUrl='https://dtr.docker.si2m.tec'
	def dockerRegistryImageBuild='test-store/si2m-npm10-build'
	def dockerRegistryRepo='repo-store'
	def dockerRegistryUser='7c1.03ee-4888-4843-951f-2f7b4f846b8c'
	def nexusRepo='http://vlc3inf013:8081/repository/npm-si2m'
	def gitBitbucketUrl='https://bitbucket.org/si2m'
}
