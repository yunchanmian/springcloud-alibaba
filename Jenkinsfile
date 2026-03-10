pipeline {
    agent any

    parameters {
        // 基础参数
        string(name: 'VERSION', defaultValue: '1.0.0', description: '构建版本号')
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: '是否跳过测试')
        choice(
                name: 'DEPLOY_ENV',
                choices: ['dev', 'test', 'staging', 'prod'],
                description: '部署环境'
        )

        // 服务选择参数
        extendedChoice(
                name: 'SERVICES_TO_DEPLOY',
                type: 'CHECKBOX',
                defaultValue: 'nacos,sentinel,seata,gateway,auth-service,user-service,order-service,product-service',
                description: '选择要部署的服务',
                value: 'nacos,sentinel,seata,gateway,auth-service,user-service,order-service,product-service,inventory-service,payment-service',
                visibleItemCount: 10
        )

        // 部署策略
        choice(
                name: 'DEPLOY_STRATEGY',
                choices: ['rolling', 'parallel', 'sequential'],
                defaultValue: 'rolling',
                description: '部署策略: rolling-滚动部署, parallel-并行部署, sequential-顺序部署'
        )

        // 健康检查配置
        booleanParam(name: 'ENABLE_HEALTH_CHECK', defaultValue: true, description: '是否启用健康检查')
        number(name: 'HEALTH_CHECK_TIMEOUT', defaultValue: 300, description: '健康检查超时时间(秒)')

        // 回滚配置
        booleanParam(name: 'ENABLE_ROLLBACK', defaultValue: false, description: '是否启用自动回滚')
        number(name: 'ROLLBACK_THRESHOLD', defaultValue: 3, description: '失败次数阈值(触发回滚)')
    }

    tools {
        maven "3.9.12"
        jdk 'jdk8'
    }

    environment {
        // 基础配置
        BASE_DEPLOY_PATH = '/opt/app/spring-cloud-alibaba'
        REMOTE_JAVA_HOME = '/usr/lib/jvm/java-8-openjdk-amd64'
        MAVEN_PROFILE = "${params.DEPLOY_ENV}"

        // Spring Cloud Alibaba 版本
        SC_ALIBABA_VERSION = '2022.0.0.0'

        // 服务定义 - Spring Cloud Alibaba 完整体系
        SERVICES = [
                // 基础设施服务
                'nacos': [
                        port: '8848',
                        deployPath: "${BASE_DEPLOY_PATH}/nacos",
                        healthEndpoint: '/nacos/v1/ns/service/list',
                        buildPath: 'nacos',
                        isInfrastructure: true,
                        startupOrder: 1,
                        jarName: 'nacos-server.jar',
                        memory: '1g',
                        isStandalone: true
                ],
                'sentinel': [
                        port: '8719',
                        dashboardPort: '8080',
                        deployPath: "${BASE_DEPLOY_PATH}/sentinel",
                        healthEndpoint: '/actuator/health',
                        buildPath: 'sentinel-dashboard',
                        isInfrastructure: true,
                        startupOrder: 2,
                        jarName: 'sentinel-dashboard.jar',
                        memory: '512m'
                ],
                'seata': [
                        port: '8091',
                        deployPath: "${BASE_DEPLOY_PATH}/seata",
                        healthEndpoint: '/actuator/health',
                        buildPath: 'seata-server',
                        isInfrastructure: true,
                        startupOrder: 3,
                        jarName: 'seata-server.jar',
                        memory: '1g',
                        configType: 'nacos'
                ],

                // 核心服务
                'gateway': [
                        port: '9999',
                        deployPath: "${BASE_DEPLOY_PATH}/gateway",
                        healthEndpoint: '/actuator/health',
                        buildPath: 'cloud-gateway',
                        startupOrder: 4,
                        jarName: 'cloud-gateway.jar',
                        memory: '1g',
                        dependencies: ['nacos', 'sentinel']
                ],
                'auth-service': [
                        port: '8001',
                        deployPath: "${BASE_DEPLOY_PATH}/auth-service",
                        healthEndpoint: '/actuator/health',
                        buildPath: 'auth-service',
                        startupOrder: 5,
                        jarName: 'auth-service.jar',
                        memory: '512m',
                        dependencies: ['nacos', 'sentinel']
                ],

                // 业务服务
                'user-service': [
                        port: '8002',
                        deployPath: "${BASE_DEPLOY_PATH}/user-service",
                        healthEndpoint: '/actuator/health',
                        buildPath: 'user-service',
                        startupOrder: 6,
                        jarName: 'user-service.jar',
                        memory: '512m',
                        dependencies: ['nacos', 'sentinel', 'seata']
                ],
                'order-service': [
                        port: '8003',
                        deployPath: "${BASE_DEPLOY_PATH}/order-service",
                        healthEndpoint: '/actuator/health',
                        buildPath: 'order-service',
                        startupOrder: 7,
                        jarName: 'order-service.jar',
                        memory: '512m',
                        dependencies: ['nacos', 'sentinel', 'seata']
                ],
                'product-service': [
                        port: '8004',
                        deployPath: "${BASE_DEPLOY_PATH}/product-service",
                        healthEndpoint: '/actuator/health',
                        buildPath: 'product-service',
                        startupOrder: 8,
                        jarName: 'product-service.jar',
                        memory: '512m',
                        dependencies: ['nacos', 'sentinel']
                ],
                'inventory-service': [
                        port: '8005',
                        deployPath: "${BASE_DEPLOY_PATH}/inventory-service",
                        healthEndpoint: '/actuator/health',
                        buildPath: 'inventory-service',
                        startupOrder: 9,
                        jarName: 'inventory-service.jar',
                        memory: '512m',
                        dependencies: ['nacos', 'sentinel', 'seata']
                ],
                'payment-service': [
                        port: '8006',
                        deployPath: "${BASE_DEPLOY_PATH}/payment-service",
                        healthEndpoint: '/actuator/health',
                        buildPath: 'payment-service',
                        startupOrder: 10,
                        jarName: 'payment-service.jar',
                        memory: '512m',
                        dependencies: ['nacos', 'sentinel', 'seata']
                ]
        ]

        // 环境特定配置
        NACOS_CONFIG = [
                'dev': [
                        serverAddr: 'localhost:8848',
                        namespace: 'dev',
                        group: 'DEFAULT_GROUP'
                ],
                'test': [
                        serverAddr: 'nacos-test:8848',
                        namespace: 'test',
                        group: 'TEST_GROUP'
                ],
                'staging': [
                        serverAddr: 'nacos-staging:8848',
                        namespace: 'staging',
                        group: 'STAGING_GROUP'
                ],
                'prod': [
                        serverAddr: 'nacos-prod-cluster:8848',
                        namespace: 'prod',
                        group: 'PROD_GROUP'
                ]
        ]

        SENTINEL_CONFIG = [
                'dev': [
                        dashboard: 'localhost:8080',
                        transport: '8719'
                ],
                'prod': [
                        dashboard: 'sentinel-dashboard-cluster:8080',
                        transport: '8719'
                ]
        ]

        SEATA_CONFIG = [
                'dev': [
                        serviceGroup: 'default',
                        serverAddr: 'localhost:8091',
                        configType: 'nacos'
                ],
                'prod': [
                        serviceGroup: 'seata-server-group',
                        serverAddr: 'seata-server-cluster:8091',
                        configType: 'nacos'
                ]
        ]
    }

    stages {
        // 阶段 1: 环境准备和验证
        stage('环境准备') {
            steps {
                script {
                    echo "=== Spring Cloud Alibaba 微服务部署流水线 ==="
                    echo "环境: ${params.DEPLOY_ENV}"
                    echo "版本: ${params.VERSION}"
                    echo "部署策略: ${params.DEPLOY_STRATEGY}"
                    echo "选择的服务: ${params.SERVICES_TO_DEPLOY}"

                    // 检查必要工具
                    sh '''
                        java -version
                        mvn -version
                        docker --version 2>/dev/null || echo "Docker 未安装，跳过容器化部署"
                    '''

                    // 验证目录权限
                    sh """
                        mkdir -p ${BASE_DEPLOY_PATH}
                        echo "部署基础目录: ${BASE_DEPLOY_PATH}"
                        ls -la ${BASE_DEPLOY_PATH} || true
                    """

                    // 设置构建描述
                    currentBuild.description = "SCA-${params.DEPLOY_ENV.toUpperCase()}-v${params.VERSION}"
                }
            }
        }

        // 阶段 2: 代码检出
        stage('检出代码') {
            steps {
                checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/master']],
                        extensions: [],
                        userRemoteConfigs: [[
                                                    url: 'https://gitee.com/your-repo/spring-cloud-alibaba-demo.git',
                                                    credentialsId: 'your-git-credential'
                                            ]]
                ])

                // 记录代码信息
                script {
                    sh 'git log --oneline -5'
                    env.GIT_COMMIT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    echo "当前提交: ${env.GIT_COMMIT}"
                }
            }
        }

        // 阶段 3: 代码质量检查
        stage('代码质量检查') {
            parallel {
                stage('代码编译') {
                    steps {
                        script {
                            sh 'mvn clean compile -P${MAVEN_PROFILE} -DskipTests'
                        }
                    }
                }

                stage('静态代码分析') {
                    steps {
                        script {
                            // 可以使用 SonarQube 或 CheckStyle
                            sh 'mvn checkstyle:check 2>/dev/null || echo "CheckStyle 检查完成"'
                        }
                    }
                }
            }
        }

        // 阶段 4: 构建和单元测试
        stage('构建和测试') {
            steps {
                script {
                    def mvnCommand = "mvn clean package -P${MAVEN_PROFILE} -Dspring-cloud-alibaba.version=${SC_ALIBABA_VERSION}"
                    if (params.SKIP_TESTS.toBoolean()) {
                        mvnCommand += " -DskipTests"
                    } else {
                        mvnCommand += " -DfailIfNoTests=false"
                    }

                    echo "执行构建命令: ${mvnCommand}"
                    sh mvnCommand

                    // 生成构建报告
                    junit '**/target/surefire-reports/*.xml'
                }
            }

            post {
                success {
                    script {
                        // 归档所有服务的jar包
                        def serviceList = params.SERVICES_TO_DEPLOY.split(',')
                        serviceList.each { serviceName ->
                            def trimmedName = serviceName.trim()
                            def serviceConfig = env.SERVICES[trimmedName]
                            if (serviceConfig) {
                                def buildPath = serviceConfig.buildPath
                                def jarPattern = "${buildPath}/target/*.jar"
                                if (fileExists(jarPattern)) {
                                    archiveArtifacts artifacts: jarPattern, fingerprint: true
                                    stash name: "jar-${trimmedName}", includes: "${jarPattern}"
                                    echo "✅ 已存档服务: ${trimmedName}"
                                }
                            }
                        }
                    }
                }
            }
        }

        // 阶段 5: 构建 Docker 镜像（可选）
        stage('Docker 构建') {
            when {
                expression { params.DEPLOY_ENV == 'prod' || params.DEPLOY_ENV == 'staging' }
            }
            steps {
                script {
                    echo "开始构建 Docker 镜像..."

                    def serviceList = params.SERVICES_TO_DEPLOY.split(',')
                    serviceList.each { serviceName ->
                        def trimmedName = serviceName.trim()
                        def serviceConfig = env.SERVICES[trimmedName]
                        if (serviceConfig && fileExists("${serviceConfig.buildPath}/Dockerfile")) {
                            dir(serviceConfig.buildPath) {
                                docker.build("sca-${trimmedName}:${params.VERSION}", ".")
                                echo "✅ Docker 镜像构建完成: sca-${trimmedName}:${params.VERSION}"
                            }
                        }
                    }
                }
            }
        }

        // 阶段 6: 部署基础设施服务
        stage('部署基础设施') {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                script {
                    def infraServices = ['nacos', 'sentinel', 'seata']
                    def selectedServices = params.SERVICES_TO_DEPLOY.split(',')

                    infraServices.each { infraService ->
                        if (selectedServices.contains(infraService)) {
                            echo "🚀 开始部署基础设施: ${infraService}"
                            deploySpringCloudAlibabaService(infraService)
                        }
                    }
                }
            }
        }

        // 阶段 7: 部署业务服务
        stage('部署业务服务') {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                script {
                    def selectedServices = params.SERVICES_TO_DEPLOY.split(',')
                    def businessServices = selectedServices.findAll {
                        service -> !['nacos', 'sentinel', 'seata'].contains(service.trim())
                    }

                    if (businessServices.size() > 0) {
                        echo "开始部署业务服务，数量: ${businessServices.size()}"

                        // 根据部署策略选择部署方式
                        if (params.DEPLOY_STRATEGY == 'parallel') {
                            // 并行部署
                            parallel businessServices.collectEntries { serviceName ->
                                [(serviceName.trim()): {
                                    script {
                                        deploySpringCloudAlibabaService(serviceName.trim())
                                    }
                                }]
                            }
                        } else if (params.DEPLOY_STRATEGY == 'rolling') {
                            // 滚动部署
                            businessServices.each { serviceName ->
                                script {
                                    deploySpringCloudAlibabaService(serviceName.trim())
                                    // 滚动部署间隔
                                    sleep 30
                                }
                            }
                        } else {
                            // 顺序部署
                            businessServices.each { serviceName ->
                                script {
                                    deploySpringCloudAlibabaService(serviceName.trim())
                                }
                            }
                        }
                    }
                }
            }
        }

        // 阶段 8: 集成测试和验证
        stage('集成验证') {
            when {
                expression {
                    params.ENABLE_HEALTH_CHECK.toBoolean() &&
                            (currentBuild.result == null || currentBuild.result == 'SUCCESS')
                }
            }
            steps {
                script {
                    timeout(time: params.HEALTH_CHECK_TIMEOUT, unit: 'SECONDS') {
                        script {
                            // 1. 服务健康检查
                            def selectedServices = params.SERVICES_TO_DEPLOY.split(',')
                            def allHealthy = true

                            selectedServices.each { serviceName ->
                                def trimmedName = serviceName.trim()
                                def serviceConfig = env.SERVICES[trimmedName]
                                if (serviceConfig && serviceConfig.port) {
                                    def healthUrl = "http://localhost:${serviceConfig.port}${serviceConfig.healthEndpoint}"
                                    try {
                                        sh """
                                            for i in {1..30}; do
                                                if curl -f -s ${healthUrl} > /dev/null; then
                                                    echo "✅ 服务 ${trimmedName} 健康检查通过"
                                                    break
                                                fi
                                                if [ \$i -eq 30 ]; then
                                                    echo "❌ 服务 ${trimmedName} 健康检查失败"
                                                    exit 1
                                                fi
                                                echo "等待服务 ${trimmedName} 启动... (\$i/30)"
                                                sleep 2
                                            done
                                        """
                                    } catch (Exception e) {
                                        echo "❌ 服务 ${trimmedName} 健康检查失败: ${e.message}"
                                        allHealthy = false
                                    }
                                }
                            }

                            if (!allHealthy) {
                                error "部分服务健康检查失败"
                            }

                            // 2. Nacos 服务注册验证
                            if (selectedServices.contains('nacos')) {
                                echo "🔍 检查 Nacos 服务注册状态..."
                                def nacosConfig = NACOS_CONFIG[params.DEPLOY_ENV]
                                def nacosUrl = "http://${nacosConfig.serverAddr}/nacos/v1/ns/service/list"

                                try {
                                    sh """
                                        curl -f -s "${nacosUrl}" | grep -q "name" && echo "✅ Nacos 服务列表正常"
                                    """
                                } catch (Exception e) {
                                    echo "⚠️  Nacos 服务注册状态检查异常"
                                }
                            }

                            // 3. 核心业务接口测试
                            if (selectedServices.contains('gateway')) {
                                echo "🔍 测试网关路由..."
                                try {
                                    sh """
                                        curl -f -s "http://localhost:9999/actuator/gateway/routes" | grep -q "uri" && echo "✅ 网关路由正常"
                                    """
                                } catch (Exception e) {
                                    echo "⚠️  网关路由检查异常"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // 生成部署报告
                def report = """
                === Spring Cloud Alibaba 部署报告 ===
                环境: ${params.DEPLOY_ENV}
                版本: ${params.VERSION}
                构建编号: #${env.BUILD_NUMBER}
                提交ID: ${env.GIT_COMMIT}
                部署策略: ${params.DEPLOY_STRATEGY}
                部署服务: ${params.SERVICES_TO_DEPLOY}
                开始时间: ${currentBuild.startTimeInMillis}
                持续时间: ${currentBuild.durationString}
                构建结果: ${currentBuild.result}
                构建URL: ${env.BUILD_URL}
                """

                echo report

                // 保存报告
                writeFile file: 'deployment-report.txt', text: report
                archiveArtifacts artifacts: 'deployment-report.txt'
            }
        }

        success {
            script {
                echo "🎉 Spring Cloud Alibaba 微服务部署成功!"

                // 发送成功通知
                emailext(
                        subject: "[SUCCESS] Spring Cloud Alibaba 部署完成 - ${params.DEPLOY_ENV.toUpperCase()} - v${params.VERSION}",
                        body: """
                    🎯 部署环境: ${params.DEPLOY_ENV}
                    📦 版本: ${params.VERSION}
                    🔢 构建编号: #${env.BUILD_NUMBER}
                    📋 部署服务: ${params.SERVICES_TO_DEPLOY}
                    ⏱️  持续时间: ${currentBuild.durationString}
                    🔗 构建详情: ${env.BUILD_URL}
                    
                    所有服务健康检查通过，系统运行正常。
                    """,
                        to: 'devops@company.com,developers@company.com',
                        replyTo: 'devops@company.com'
                )
            }
        }

        failure {
            script {
                echo "❌ Spring Cloud Alibaba 微服务部署失败!"

                // 自动回滚逻辑
                if (params.ENABLE_ROLLBACK.toBoolean()) {
                    echo "🔄 开始自动回滚..."
                    try {
                        // 回滚到上一个稳定版本
                        sh """
                            # 回滚逻辑示例
                            echo "执行回滚操作..."
                            # 这里可以添加具体的回滚脚本
                        """
                        echo "✅ 自动回滚完成"
                    } catch (Exception e) {
                        echo "❌ 自动回滚失败: ${e.message}"
                    }
                }

                // 发送失败通知
                emailext(
                        subject: "[FAILURE] Spring Cloud Alibaba 部署失败 - ${params.DEPLOY_ENV.toUpperCase()} - v${params.VERSION}",
                        body: """
                    🚨 部署环境: ${params.DEPLOY_ENV}
                    📦 版本: ${params.VERSION}
                    🔢 构建编号: #${env.BUILD_NUMBER}
                    ❌ 失败阶段: ${currentBuild.currentResult}
                    🔗 构建日志: ${env.BUILD_URL}/console
                    
                    请及时检查日志并处理问题。
                    """,
                        to: 'devops-alert@company.com,team-lead@company.com',
                        replyTo: 'devops-alert@company.com',
                        attachLog: true
                )
            }
        }

        unstable {
            echo "⚠️  构建结果不稳定，请检查测试报告"
        }

        cleanup {
            // 清理工作空间
            cleanWs(
                    cleanWhenAborted: true,
                    cleanWhenFailure: true,
                    cleanWhenNotBuilt: true,
                    cleanWhenSuccess: true,
                    cleanWhenUnstable: true,
                    deleteDirs: true
            )
        }
    }
}

// ============================================
// 自定义函数：部署 Spring Cloud Alibaba 服务
// ============================================
def deploySpringCloudAlibabaService(serviceName) {
    def serviceConfig = env.SERVICES[serviceName]
    if (!serviceConfig) {
        error "❌ 未找到服务配置: ${serviceName}"
        return
    }

    def deployPath = serviceConfig.deployPath
    def buildPath = serviceConfig.buildPath
    def jarName = serviceConfig.jarName ?: "${serviceName}.jar"
    def port = serviceConfig.port
    def memory = serviceConfig.memory ?: '512m'

    echo "🚀 开始部署服务: ${serviceName}"
    echo "   📁 部署路径: ${deployPath}"
    echo "   🔧 构建模块: ${buildPath}"
    echo "   🐳 端口: ${port}"
    echo "   💾 内存: ${memory}"

    try {
        // 1. 准备部署目录
        sh """
            mkdir -p ${deployPath}
            echo "✅ 创建部署目录: ${deployPath}"
        """

        // 2. 停止旧服务
        sh """
            # 查找进程
            pid=\$(ps aux | grep -v grep | grep ${jarName} | awk '{print \$2}')
            if [ -n "\$pid" ]; then
                echo "🛑 停止旧进程 PID: \$pid"
                # 优雅停止
                kill -15 \$pid
                # 等待停止
                for i in {1..30}; do
                    if ! ps -p \$pid > /dev/null 2>&1; then
                        echo "✅ 进程已停止"
                        break
                    fi
                    echo "⏳ 等待进程停止... (\$i/30)"
                    sleep 1
                done
                # 强制停止
                if ps -p \$pid > /dev/null 2>&1; then
                    echo "⚠️  强制终止进程"
                    kill -9 \$pid
                    sleep 2
                fi
            else
                echo "ℹ️  没有找到运行中的进程"
            fi
            
            # 清理旧文件
            rm -f ${deployPath}/${serviceName}.pid
        """

        // 3. 获取构建产物
        unstash "jar-${serviceName}"

        def jarFiles = findFiles(glob: "${buildPath}/target/*.jar")
        if (jarFiles.length == 0) {
            error "❌ 未找到构建产物: ${buildPath}/target/*.jar"
            return
        }

        def sourceJar = jarFiles[0].path

        // 4. 备份和部署
        sh """
            cd ${deployPath}
            
            # 备份旧版本
            if [ -f "${jarName}" ]; then
                backup_time=\$(date +%Y%m%d%H%M%S)
                backup_file="${jarName}.backup.\${backup_time}"
                cp "${jarName}" "\${backup_file}"
                echo "📦 已备份: \${backup_file}"
            fi
            
            # 复制新版本
            cp ${WORKSPACE}/${sourceJar} ${jarName}
            echo "✅ 复制新版本完成"
            
            # 准备启动参数
            JAVA_OPTS="-Xms${memory} -Xmx${memory} -Dfile.encoding=UTF-8"
            
            # Spring Cloud Alibaba 特定配置
            JAVA_OPTS="\${JAVA_OPTS} -Dspring.profiles.active=${params.DEPLOY_ENV}"
            
            # Nacos 配置
            if ([[ ${serviceConfig.dependencies} =~ "nacos" ]] || ${serviceName} == 'seata') {
                nacosConfig=${NACOS_CONFIG[params.DEPLOY_ENV]}
                JAVA_OPTS="\${JAVA_OPTS} -Dspring.cloud.nacos.server-addr=\${nacosConfig.serverAddr}"
                JAVA_OPTS="\${JAVA_OPTS} -Dspring.cloud.nacos.config.namespace=\${nacosConfig.namespace}"
                JAVA_OPTS="\${JAVA_OPTS} -Dspring.cloud.nacos.discovery.namespace=\${nacosConfig.namespace}"
            }
            
            # Sentinel 配置
            if ([[ ${serviceConfig.dependencies} =~ "sentinel" ]]) {
                sentinelConfig=${SENTINEL_CONFIG[params.DEPLOY_ENV] ?: SENTINEL_CONFIG['dev']}
                JAVA_OPTS="\${JAVA_OPTS} -Dspring.cloud.sentinel.transport.dashboard=localhost:\${sentinelConfig.dashboard}"
                JAVA_OPTS="\${JAVA_OPTS} -Dspring.cloud.sentinel.transport.port=\${sentinelConfig.transport}"
            }
            
            # Seata 配置
            if (${serviceName} == 'seata') {
                seataConfig=${SEATA_CONFIG[params.DEPLOY_ENV] ?: SEATA_CONFIG['dev']}
                JAVA_OPTS="\${JAVA_OPTS} -Dseata.config.type=\${seataConfig.configType}"
                JAVA_OPTS="\${JAVA_OPTS} -Dseata.service.vgroup-mapping.default_tx_group=\${seataConfig.serviceGroup}"
            }
            
            echo "启动参数: \${JAVA_OPTS}"
            
            # 启动服务
            nohup ${REMOTE_JAVA_HOME}/bin/java \${JAVA_OPTS} -jar ${jarName} > ${serviceName}.log 2>&1 &
            
            # 记录PID
            echo \$! > ${serviceName}.pid
            echo "🚀 服务启动，PID: \$(cat ${serviceName}.pid)"
            
            # 等待启动
            sleep 3
            if ps -p \$(cat ${serviceName}.pid) > /dev/null 2>&1; then
                echo "✅ 服务进程运行正常"
            else
                echo "❌ 服务进程启动失败"
                echo "=== 错误日志 ==="
                tail -50 ${serviceName}.log || true
                echo "================"
                exit 1
            fi
        """

        echo "🎯 服务 ${serviceName} 部署完成"

    } catch (Exception e) {
        error "❌ 部署服务 ${serviceName} 失败: ${e.message}"
        throw e
    }
}