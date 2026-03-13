// Jenkinsfile (Declarative Pipeline)
// 适用于 Spring Cloud Alibaba 微服务项目，自动构建多模块并部署到 本机

pipeline {
    // 指定在任何可用的 agent 上执行
    agent any

    tools {
        // 使用全局工具中配置的 Maven（名称为 "3.9.12"）并将其添加到 PATH 环境变量
        maven "3.9.12"
        // 使用全局工具中配置的 JDK 8（名称为 'jdk8'）并设置 JAVA_HOME
        jdk 'jdk8'
    }

    // 定义全局环境变量
    environment {
        // 镜像仓库命名空间（通常为项目名）
        PROJECT_NAME = 'gray-level-dome'
        // 部署目录 - 使用双引号确保变量被展开
        DEPLOY_PATH = "/opt/app/${PROJECT_NAME}"
        // 日志目录 - 使用双引号确保变量被展开
        LOG_DIR = "/opt/logs/${PROJECT_NAME}"
        // 备份目录 - 使用双引号确保变量被展开
        BACKUP_DIR = "/opt/backup/${PROJECT_NAME}"
        // 服务器 JDK 路径
        REMOTE_JAVA_HOME = '/usr/lib/jvm/java-8-openjdk-amd64'
        // Git 分支名称，用于镜像标签
        BRANCH_NAME = "${env.BRANCH_NAME}"
        // 构建号，用于镜像标签唯一性
        BUILD_NUMBER = "${env.BUILD_NUMBER}"
        // 微服务模块列表（项目子模块），使用逗号分隔的字符串
        SERVICE_MODULES = 'gray-gateway,gray-consumer,gray-provider'
        // 服务端口映射（格式：服务名:端口，用于停止服务和健康检查）
        SERVICE_PORTS = 'gray-gateway:48080,gray-consumer:48082,gray-provider:48081'
        // 是否跳过单元测试（可参数化）
        SKIP_TESTS = 'false'
        // 此变量将用于Maven命令，激活对应的profile
        ACTIVE_MAVEN_PROFILE = "${params.DEPLOY_ENV}"
        // JVM参数
        JAVA_OPTS = '-Xms512m -Xmx1024m -XX:+UseG1GC -Dfile.encoding=UTF-8'
    }

    // 参数化构建，允许用户手动选择要构建的服务或指定版本
    parameters {
        // 选择要构建的服务，默认为全部
        choice(
                name: 'SERVICES_TO_DEPLOY',
                choices: ['all', 'gray-gateway', 'gray-consumer', 'gray-provider'],
                description: '选择要构建的微服务（all 表示全部）'
        )
        // 部署环境
        choice(
                name: 'DEPLOY_ENV',
                choices: ['local', 'gray', 'stable'],
                description: '选择部署环境'
        )
        // Git分支参数
        string(
                name: 'BRANCH_NAME',
                defaultValue: 'main',
                description: '要构建的Git分支名'
        )
        // 是否跳过测试
        booleanParam(
                name: 'SKIP_TESTS',
                defaultValue: false,
                description: '跳过单元测试'
        )
        // 是否执行Sonar扫描
        booleanParam(
                name: 'SONAR_SCAN',
                defaultValue: false,
                description: '执行SonarQube代码质量检查'
        )
        // 部署前确认
        booleanParam(
                name: 'DEPLOY_CONFIRM',
                defaultValue: false,
                description: '确认执行部署（安全开关）'
        )
    }

    // 流水线选项配置
    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '10'))
        disableConcurrentBuilds()
        timestamps()
    }

    stages {
        // 阶段1：检出代码
        stage('Checkout') {
            steps {
//                // 从 Git 仓库拉取代码
//                echo '开始从Git仓库拉取代码...'
//                // 从Git仓库拉取代码，支持分支选择和凭证认证
//                checkout scmGit(branches: [[
//                    name: "*/${params.BRANCH_NAME ?: 'main'}"]],
//                    extensions: [],
//                    userRemoteConfigs: [[
//                        credentialsId: '8a75608f-a8cc-45dd-9a40-ade046ffb18a',
//                        url: 'https://github.com/yunchanmian/springcloud-alibaba.git']
//                    ])

                // 记录构建信息
                script {
                    sh """
                        echo "=== 构建信息 ===" > build-info.txt
                        echo "项目: ${PROJECT_NAME}" >> build-info.txt
                        echo "构建编号: ${BUILD_NUMBER}" >> build-info.txt
                        echo "Git分支: ${env.BRANCH_NAME}" >> build-info.txt
                        echo "Git提交: \${GIT_COMMIT}" >> build-info.txt
                        echo "构建时间: \$(date +\"%Y-%m-%d %H:%M:%S\")" >> build-info.txt
                        echo "构建用户: \${BUILD_USER_ID}" >> build-info.txt
                        echo "工作空间: ${env.WORKSPACE}" >> build-info.txt
                    """
                    // 显示构建信息
                    echo "检出代码完成，分支：${env.BRANCH_NAME}"
                    echo "提交ID：${env.GIT_COMMIT}"
                    echo "  - 工作目录: ${env.WORKSPACE}"
                }
                echo '代码拉取完成！'
            }
        }


        // 阶段2：环境检查
        stage('环境检查') {
            steps {
                script {
                    echo '开始检查构建环境...'

                    // 检查Java和Maven版本
                    sh """
                        echo "=== 环境信息 ==="
                        java -version
                        echo ""
                        mvn --version
                        echo ""
                        echo "工作目录:"
                        pwd
                        echo ""
                        echo "项目结构:"
                        ls -la
                    """
                    // 检查微服务模块目录
                    echo "检查微服务模块..."
                    echo ""

                    echo "检查微服务模块..."
                    echo ""


                    // 确定要处理的服务列表
                    def services = getServicesToProcess()

                    def moduleCheckScript = """
                            MODULE_COUNT=0
                    """

                    services.each { service ->
                        def serviceName = service.trim()
                        moduleCheckScript += """
                            if [ -d "${serviceName}" ]; then
                                echo "✅ 找到模块: ${serviceName}"
                                MODULE_COUNT=\$((MODULE_COUNT + 1))
                                
                                # 检查模块结构
                                if [ -f "${serviceName}/pom.xml" ]; then
                                    echo "  - ✅ 包含pom.xml"
                                     # 检查pom.xml中是否有spring-boot-maven-plugin
                                    if grep -q "spring-boot-maven-plugin" "${serviceName}/pom.xml"; then
                                        echo "  - ✅ 配置了spring-boot-maven-plugin"
                                    else
                                        echo "  - ⚠️ 未配置spring-boot-maven-plugin"
                                    fi
                                else
                                    echo "  - ❌ 缺少pom.xml"
                                fi
                                
                                if [ -d "${serviceName}/src/main/java" ]; then
                                    echo "  - ✅ 包含Java源码"
                                else
                                    echo "  - ⚠️ 缺少Java源码"
                                fi
                            else
                                echo "❌ 未找到模块: ${serviceName}"
                            fi
                        """
                    }

                    moduleCheckScript += """
                        echo ""
                        echo "找到模块数: \${MODULE_COUNT}/${services.size()}"
                        
                        if [ \${MODULE_COUNT} -eq 0 ]; then
                            echo "错误: 未找到任何微服务模块！"
                            exit 1
                        fi
                    """

                    sh moduleCheckScript

                    echo '环境检查完成！'
                }
            }
        }

//        // 阶段2：代码质量检查（可选）
//        stage('代码质量检查') {
//            when {
//                expression { params.SONAR_SCAN == true }
//            }
//            steps {
//                script {
//                    echo '开始代码质量检查...'
//                    withSonarQubeEnv('SonarQube-Server') {
//                        sh """
//                            mvn clean verify sonar:sonar \
//                                -Dsonar.projectKey=${PROJECT_NAME} \
//                                -Dsonar.projectName=${PROJECT_NAME} \
//                                -Dsonar.host.url=${SONAR_HOST_URL} \
//                                -Dsonar.login=${SONAR_AUTH_TOKEN}
//                        """
//                    }
//                    echo '代码质量检查完成！'
//                }
//            }
//        }

        // 阶段3：依赖检查
        stage('依赖检查') {
            steps {
                script {
                    echo '开始检查项目依赖...'
                    sh '''
                        echo "检查Spring Cloud和Alibaba依赖..."
                        # 检查依赖树，查看是否有版本冲突
                        mvn dependency:tree -Dincludes=org.springframework.cloud,com.alibaba.cloud > dependency-tree.txt
                        
                        echo "检查依赖版本..."
                        mvn versions:display-dependency-updates -Dversions.displayDependencyUpdates=false \\
                            -Dversions.displayPropertyUpdates=true \\
                            -Dversions.displayPluginUpdates=false
                        
                        echo ""
                        echo "依赖检查完成"
                        echo "依赖树已保存到: dependency-tree.txt"
                    '''
                    echo '✅ 依赖检查完成！'
                }
            }
            post {
                always {
                    // 保存依赖树文件
                    archiveArtifacts artifacts: 'dependency-tree.txt', fingerprint: true
                }
            }
        }


        // 阶段5：Maven编译打包
        stage('Maven Build') {
            steps {
                script {
                    echo '开始编译Spring Cloud Alibaba微服务项目...'

                    // 构建命令
                    def mvnCommand = "mvn clean package"

                    // 添加Maven Profile
                    if (params.DEPLOY_ENV) {
                        mvnCommand += " -P${params.DEPLOY_ENV}"
                        echo "激活Maven Profile: ${params.DEPLOY_ENV}"
                    }

                    // 是否跳过测试
                    if (params.SKIP_TESTS) {
                        mvnCommand += " -DskipTests"
                        echo "跳过单元测试"
                    }

                    // 执行构建
                    echo "执行命令: ${mvnCommand}"
                    sh mvnCommand

                    def buildCheckScript = """
                        echo "=== 构建产物检查 ==="
                        echo ""
                        
                        BUILD_SUCCESS=true
                    """
                    // 获取所有服务列表
                    def services = env.SERVICE_MODULES.split(',')


                    services.each { service ->
                        def serviceName = service.trim()
                        buildCheckScript += """
                            if [ -d "${serviceName}/target" ]; then
                                JAR_FILE=\$(find "${serviceName}/target" -name "*.jar" -type f | grep -v "original-" | grep -v "sources" | grep -v "javadoc" | head -1)
                                if [ -f "\${JAR_FILE}" ]; then
                                    echo "✅ ${serviceName}:"
                                    echo "  - 文件: \$(basename \${JAR_FILE})"
                                    echo "  - 大小: \$(ls -lh "\${JAR_FILE}" | awk '{print \$5}')"
                                    echo "  - 修改时间: \$(stat -c %y "\${JAR_FILE}" | cut -d. -f1)"
                                else
                                    echo "❌ ${serviceName}: 未找到可执行JAR文件"
                                    BUILD_SUCCESS=false
                                fi
                            else
                                echo "❌ ${serviceName}: 无target目录"
                                BUILD_SUCCESS=false
                            fi
                        """
                    }

                    buildCheckScript += """
                        echo ""
                        if [ "\${BUILD_SUCCESS}" = "true" ]; then
                            echo "✅ 所有模块构建成功"
                        else
                            echo "❌ 部分模块构建失败！"
                            exit 1
                        fi
                    """

                    sh buildCheckScript

                    echo '✅ 编译构建完成！'

                    // 更新构建描述
                    currentBuild.description = "环境: ${params.DEPLOY_ENV} | 服务: ${params.SERVICES_TO_DEPLOY} | 分支: ${params.BRANCH_NAME}"
                }
            }

            post {
                success {
                    // 归档构建产物
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                    archiveArtifacts artifacts: 'build-info.txt', fingerprint: true
                    echo '✅ 构建产物已归档'
                }
                failure {
                    echo '❌ 编译构建失败，请检查Maven日志'
                }
            }
        }
        // 阶段6：部署确认
        stage('部署确认') {
            steps {
                script {
                    echo '⚠️ 部署前确认 ⚠️'
                    echo "即将部署以下服务: ${params.SERVICES_TO_DEPLOY}"
                    echo "构建编号: ${BUILD_NUMBER}"
                    echo "部署环境: ${params.DEPLOY_ENV}"
                    echo "目标路径: ${DEPLOY_PATH}"

                    // 检查是否需要人工确认
                    if (params.DEPLOY_ENV == 'stable') {
                        input message: '确认部署到生产环境？', ok: '确认部署'
                    } else if (!params.DEPLOY_CONFIRM) {
                        input message: "确认部署到 ${params.DEPLOY_ENV} 环境？", ok: '确认部署'
                    } else {
                        echo "✅ 已通过参数确认部署"
                    }
                }
            }
        }

        // 并行构建和部署（每个服务独立处理）
        stage('Build & Deploy') {
            steps {
                script {
                    // 解析端口映射
                    def portMap = getPortMap()

                    // 确定要处理的服务列表
                    def services = getServicesToProcess()

                    echo "将部署以下服务: ${services}"

                    // 定义并行任务
                    def parallelStages = [:]
                    services.each { service ->
                        def serviceName = service.trim()
                        def port = portMap[serviceName]
                        // 确保端口映射存在
                        if (port == null) {
                            error "错误：未找到服务 ${serviceName} 的端口映射，请检查 SERVICE_PORTS 配置"
                        }
                        parallelStages[serviceName] = {
                            stage("部署-${serviceName}") {
                                script {
                                    deployService(serviceName, port)
                                }
                            }
                        }
                    }
                    parallel parallelStages
                }
            }
        }


        // 阶段8：健康检查
        stage('健康检查') {
            steps {
                script {
                    echo '开始服务健康检查...'

                    // 重新解析端口映射
                    def portMap = getPortMap()

                    // 确定要检查的服务
                    def services = getServicesToProcess()

                    // 检查每个服务
                    services.each { service ->
                        def serviceName = service.trim()
                        def port = portMap[serviceName]

                        if (port) {
                            echo "检查服务 ${serviceName} (端口: ${port})..."

                            // 健康检查
                            def healthCheckPassed = false
                            def maxRetries = 10
                            def retryCount = 0

                            while (retryCount < maxRetries && !healthCheckPassed) {
                                retryCount++
                                try {
                                    // 尝试访问健康端点
                                    def response = sh(
                                            script: """
                                            curl -s -o /dev/null -w "%{http_code}" \
                                            http://localhost:${port}/${serviceName}/actuator/health \
                                            --max-time 5 || echo "000"
                                        """,
                                            returnStdout: true
                                    ).trim()

                                    if (response == "200") {
                                        echo "✅ 服务 ${serviceName} 健康检查通过 (HTTP ${response})"
                                        healthCheckPassed = true
                                    } else {
                                        echo "⏳ 服务 ${serviceName} 健康检查失败 (HTTP ${response})，重试 ${retryCount}/${maxRetries}"
                                        sleep(5) // 等待5秒
                                    }
                                } catch (Exception e) {
                                    echo "⚠️ 服务 ${serviceName} 健康检查异常: ${e.message}"
                                    sleep(5)
                                }
                            }

                            if (!healthCheckPassed) {
                                echo "❌ 服务 ${serviceName} 健康检查失败，请检查日志"
                                // 这里可以选择失败但不停止流水线
                                // currentBuild.result = 'UNSTABLE'
                            }
                        } else {
                            echo "⚠️ 未找到服务 ${serviceName} 的端口配置"
                        }
                    }

                    echo '健康检查完成！'
                }
            }
        }
    }

    // 后处理：无论构建成功或失败，都执行清理或其他操作
    post {
        always {
            // 清理工作空间（可选）
            cleanWs()
        }
        success {
            echo '恭喜，流水线执行成功！'
        }
        failure {
            echo '流水线执行失败，请检查日志。'
        }
    }
}


// ==================== 共享函数 ====================

// 获取端口映射
def getPortMap() {
    def portMap = [:]
    env.SERVICE_PORTS.split(',').each { mapping ->
        def parts = mapping.split(':')
        if (parts.size() == 2) {
            portMap[parts[0].trim()] = parts[1].trim().toInteger()
        }
    }
    return portMap
}

// 获取要处理的服务列表
def getServicesToProcess() {
    if (params.SERVICES_TO_DEPLOY == 'all') {
        return env.SERVICE_MODULES.split(',') as List
    }
    return [params.SERVICES_TO_DEPLOY]
}

// ==================== 部署服务函数 ====================
def deployService(String serviceName, int port) {
    echo "开始部署服务: ${serviceName}"

    // 定义变量
    def jarName = "${serviceName}.jar"
    def targetJar = "${serviceName}/target/${jarName}"
    def deployDir = "${env.DEPLOY_PATH}/${serviceName}"
    def pidFile = "${deployDir}/application.pid"
    def logFile = "${deployDir}/${serviceName}.log"
    def backupDir = "${env.BACKUP_DIR}/${serviceName}"
    def timestamp = sh(script: 'date +%Y%m%d_%H%M%S', returnStdout: true).trim()

    // 1. 检查JAR文件是否存在
    if (!fileExists(targetJar)) {
        error "❌ 找不到JAR文件: ${targetJar}"
        return
    }

    echo "服务信息:"
    echo "  - 服务名称: ${serviceName}"
    echo "  - 端口: ${port}"
    echo "  - 部署目录: ${deployDir}"
    echo "  - JAR文件: ${targetJar}"

    // 2. 停止旧服务
    echo "正在停止 ${serviceName} 服务..."

    sh """
        #!/bin/bash
        set -e
        
        echo "=== 停止服务 ${serviceName} ==="
        
        # 检查PID文件是否存在
        if [ -f "${pidFile}" ]; then
            PID=\$(cat "${pidFile}")
            echo "找到PID文件，进程ID: \${PID}"
            
            if ps -p \${PID} > /dev/null 2>&1; then
                echo "停止进程 \${PID}"
                kill \${PID}
                
                # 等待进程退出
                for i in {1..30}; do
                    if ! ps -p \${PID} > /dev/null 2>&1; then
                        echo "进程已正常停止"
                        break
                    fi
                    echo "等待进程停止... (\${i}/30)"
                    sleep 1
                done
                
                # 强制终止（如果仍然存在）
                if ps -p \${PID} > /dev/null 2>&1; then
                    echo "强制终止进程 \${PID}"
                    kill -9 \${PID}
                    sleep 2
                fi
            else
                echo "进程 \${PID} 不存在"
            fi
            
            # 删除PID文件
            rm -f "${pidFile}"
            echo "PID文件已删除"
        else
            echo "PID文件不存在，尝试根据端口停止进程"
            
            # 根据端口查找进程
            PORT_PID=\$(lsof -ti:${port} 2>/dev/null || echo "")
            if [ -n "\${PORT_PID}" ]; then
                echo "找到监听端口 ${port} 的进程: \${PORT_PID}"
                kill \${PORT_PID} 2>/dev/null || true
                sleep 3
                
                # 检查是否仍然运行
                if lsof -ti:${port} >/dev/null 2>&1; then
                    echo "强制终止端口 ${port} 的进程"
                    kill -9 \$(lsof -ti:${port}) 2>/dev/null || true
                fi
            else
                echo "未找到监听端口 ${port} 的进程"
            fi
        fi
        
        # 确保端口未被占用
        sleep 2
        if lsof -ti:${port} >/dev/null 2>&1; then
            echo "警告: 端口 ${port} 仍被占用"
        fi
    """

    // 3. 创建目录
    sh """
        mkdir -p "${deployDir}"
        mkdir -p "${backupDir}"
        mkdir -p "${env.LOG_DIR}/${serviceName}"
    """

    // 4. 备份旧版本
    sh """
        if [ -f "${deployDir}/${jarName}" ]; then
            BACKUP_FILE="${backupDir}/${jarName}.backup.\${timestamp}"
            echo "备份旧版本到: \${BACKUP_FILE}"
            cp "${deployDir}/${jarName}" "\${BACKUP_FILE}"
            
            # 清理旧备份（保留最近5个）
            ls -t "${backupDir}/${jarName}.backup."* 2>/dev/null | tail -n +6 | xargs rm -f 2>/dev/null || true
        fi
    """

    // 5. 复制新的JAR文件
    echo "复制新的JAR文件..."
    sh """
        cp "${targetJar}" "${deployDir}/"
        echo "JAR文件已复制到: ${deployDir}/${jarName}"
        ls -lh "${deployDir}/${jarName}"
    """

    // 6. 生成启动脚本
    def startScript = """
        #!/bin/bash
        # ${serviceName} 启动脚本
        # 生成时间: \$(date)
        
        cd "${deployDir}"
        
        # 设置JVM参数
        JAVA_OPTS="${env.JAVA_OPTS}"
        
        # 根据环境设置profile
        SPRING_PROFILE="${params.DEPLOY_ENV}"
        
        echo "启动服务: ${serviceName}"
        echo "工作目录: \$(pwd)"
        echo "JVM参数: \${JAVA_OPTS}"
        echo "Spring Profile: \${SPRING_PROFILE}"
        echo "启动时间: \$(date)"
        
        # 启动服务
        nohup ${env.REMOTE_JAVA_HOME}/bin/java \${JAVA_OPTS} \\
            -Dserver.port=${port} \\
            -Dspring.profiles.active=\${SPRING_PROFILE} \\
            -jar ${jarName} \\
            > "${logFile}" 2>&1 &
        
        # 记录PID
        echo \$! > "${pidFile}"
        echo "服务启动完成，PID: \$(cat ${pidFile})"
        echo "日志文件: ${logFile}"
    """
    // 使用多种方法确保文件创建成功
    echo "创建启动脚本..."

    // 方法1: 使用 writeFile
    try {
        writeFile file: "${deployDir}/start.sh", text: startScript
        echo "✅ writeFile 创建脚本成功"
    } catch (Exception e) {
        echo "⚠️ writeFile 失败: ${e.message}"
        // 方法2: 使用 shell 命令创建
        sh """
            cat > "${deployDir}/start.sh" << 'EOF' 
${startScript} 
EOF
        """
        echo "✅ shell 命令创建脚本成功"
    }
    // 验证文件是否创建
    sh """
        echo "=== 验证启动脚本 ==="
        echo "文件路径: ${deployDir}/start.sh"
        if [ -f "${deployDir}/start.sh" ]; then
            echo "✅ 文件存在"
            ls -lh "${deployDir}/start.sh"
            echo ""
            echo "=== 文件内容前5行 ==="
            head -5 "${deployDir}/start.sh"
        else
            echo "❌ 文件不存在，尝试重新创建..."
            cat > "${deployDir}/start.sh" << 'EOF' 
${startScript} 
EOF
            ls -lh "${deployDir}/start.sh" || { echo "❌ 重新创建失败"; exit 1; }
        fi
    """

    // 设置执行权限
    sh """
        echo "设置执行权限..."
        chmod +x "${deployDir}/start.sh"
        echo "✅ 权限设置完成"
        ls -l "${deployDir}/start.sh"
    """


    // 7. 生成停止脚本
    def stopScript = """
        #!/bin/bash
        # ${serviceName} 停止脚本
        
        echo "停止服务: ${serviceName}"
        
        if [ -f "${pidFile}" ]; then
            PID=\$(cat "${pidFile}")
            echo "找到进程ID: \${PID}"
            
            if ps -p \${PID} > /dev/null 2>&1; then
                echo "发送TERM信号到进程 \${PID}"
                kill \${PID}
                
                # 等待进程退出
                for i in {1..30}; do
                    if ! ps -p \${PID} > /dev/null 2>&1; then
                        echo "进程已停止"
                        break
                    fi
                    echo "等待进程停止... (\${i}/30)"
                    sleep 1
                done
                
                # 强制终止
                if ps -p \${PID} > /dev/null 2>&1; then
                    echo "强制终止进程 \${PID}"
                    kill -9 \${PID}
                fi
            else
                echo "进程 \${PID} 不存在"
            fi
            
            rm -f "${pidFile}"
            echo "PID文件已删除"
        else
            echo "PID文件不存在"
        fi
        
        echo "服务停止完成"
    """

    // 创建停止脚本
    echo "创建停止脚本..."
    try {
        writeFile file: "${deployDir}/stop.sh", text: stopScript
    } catch (Exception e) {
        sh """
            cat > "${deployDir}/stop.sh" << 'EOF' 
${stopScript} 
EOF
        """
    }

    sh """
        chmod +x "${deployDir}/stop.sh"
        echo "停止脚本创建完成"
    """

    // 8. 生成状态检查脚本
    def statusScript = """
        #!/bin/bash
        # ${serviceName} 状态检查脚本
        
        echo "服务状态: ${serviceName}"
        
        if [ -f "${pidFile}" ]; then
            PID=\$(cat "${pidFile}")
            if ps -p \${PID} > /dev/null 2>&1; then
                echo "状态: 运行中"
                echo "进程ID: \${PID}"
                echo "启动时间: \$(ps -o lstart= -p \${PID} 2>/dev/null || echo "未知")"
                echo "内存使用: \$(ps -o rss= -p \${PID} 2>/dev/null | awk '{printf \"%.1f MB\\n\", \$1/1024}' || echo "未知")"
                echo "CPU使用: \$(ps -o %cpu= -p \${PID} 2>/dev/null | xargs || echo "未知")%"
                
                # 检查端口
                if lsof -ti:${port} >/dev/null 2>&1; then
                    echo "端口状态: ${port} 监听中"
                    
                    # 健康检查
                    RESPONSE=\$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${port}/actuator/health 2>/dev/null || echo "000")
                    if [ "\${RESPONSE}" = "200" ]; then
                        echo "健康状态: 正常"
                    else
                        echo "健康状态: 异常 (HTTP: \${RESPONSE})"
                    fi
                else
                    echo "端口状态: ${port} 未监听"
                fi
            else
                echo "状态: 已停止 (PID文件存在但进程不存在)"
                rm -f "${pidFile}"
            fi
        else
            echo "状态: 已停止"
        fi
    """

    // 9. 启动服务
    echo "启动新服务 ${serviceName}..."
    sh """
        cd "${deployDir}"
        echo "当前目录: \$(pwd)"
        echo "执行启动脚本..."
        ./start.sh
        
        # 等待服务启动
        sleep 5
        
        # 检查是否启动成功
        if [ -f "${pidFile}" ]; then
            PID=\$(cat "${pidFile}")
            if ps -p \${PID} > /dev/null 2>&1; then
                echo "服务启动成功，进程ID: \${PID}"
            else
                echo "错误: 服务进程不存在"
                echo "=== 最后10行日志 ==="
                tail -n 10 "${logFile}" 2>/dev/null || echo "日志文件不存在"
                exit 1
            fi
        else
            echo "错误: PID文件未创建"
            exit 1
        fi
    """

    echo "✅ 服务 ${serviceName} 部署完成"
}