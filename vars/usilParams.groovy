class usilParams {
	def dockerRegistryUrl = 'https://dtr.docker.si2m.tec'
	//def dockerRegistryImageBuild='test-store/si2m-npm10-build'
	def dockerRegistryImageNodeBuild='tools-store/usil-nodejs8.12.0-build:1.0.2'
	def dockerRegistryImageMavenBuild='test-store/maven:3.5.4-jdk-8'
	def dockerRegistryRepo='repo-store'
	def dockerRegistryRepoProd='app-store'
	def dockerRegistryUser='DockerDTR'
	def nexusRepoNpmPublish='http://vlionxu001.si2m.tec:8081/nexus/content/groups/public-npm'
	def nexusRepoNpm='http://vlionxu001.si2m.tec:8081/nexus/content/repositories/Npm-si2m'
	def nexusRepo='http://vlc3inf013:8081/repository/npm-si2m'
	def nexusRepoMaven=''
	def gitBitbucketUrl='https://bitbucket.org/si2m/'
}