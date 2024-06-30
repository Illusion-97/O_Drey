def remote = [:]
remote.name = 'docker'
remote.host = '63-250-56-154.cloud-xip.com'
remote.user = 'root'
remote.password = 'D3struction971'
remote.allowAnyHosts = true

pipeline {
    agent any
    tools {
        maven 'Maven 3.9.2'
    }
    stages {
        stage('maven build') {
            steps {
                bat 'mvn clean package -Dmaven.test.skip'
            }
        }
        stage('build_image') {
            steps {
                script {
                    sshPut remote: remote, from: 'target/O_Drey.jar', into: './odrey'
                    sshPut remote: remote, from: 'Dockerfile', into: './odrey'
                    sshCommand remote: remote, command: "docker build -f ~/odrey/Dockerfile -t odrey:latest ~/odrey"
                    echo 'image build OK '
                }
            }
        }
        stage('execution') {
            steps {
                script {
                    try {
                        sshCommand remote: remote, command: "docker stop O_Drey"
                        sshCommand remote: remote, command: "docker rm O_Drey"
                    } catch(Exception e) {
                        echo "non trouvé"
                    }
                    sshCommand remote: remote, command: "echo \$DISCORD_TOKEN"
                    sshCommand remote: remote, command: "docker run --name O_Drey -d -e DISCORD_TOKEN=\$DISCORD_TOKEN -p 911:911 odrey:latest O_Drey.jar"
                }
            }
        }
    }
}
