pipeline {
    agent any  // 定义在哪个节点上执行，比如 any 表示任何可用的节点
    environment {
        // 定义环境变量
        MY_ENV_VAR = 'value'
    }

    stages {
        stage('Checkout') {
            steps {
                // 从版本库中拉取代码
                checkout scm
            }
        }
        stage('Build') {
            steps {
                // 编译构建代码
                sh './gradlew assemble'
            }
        }
    }

    post {
        success {
            echo 'Build and deployment successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
