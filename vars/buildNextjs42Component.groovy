/*
 * Toolform-compatible Jenkins 2 Pipeline build step for Next.js 4.2 based components built using Yarn
 * Expects the following scripts:
 *  - yarn clean;
 *  - yarn test; producing junit compatible test results
 *  - yarn build;
 */

def call(Map config) {
  final yarn = { cmd ->
    ansiColor('xterm') {
      dir(config.baseDir) {
        sh "yarn ${cmd}"
      }
    }
  }

  def artifactDir = "${config.project}-${config.component}-artifacts"

  container('nextjs42-builder') {

    stage('Build Details') {
      echo "Project:   ${config.project}"
      echo "Component: ${config.component}"
      echo "BuildNumber: ${config.buildNumber}"
    }

    stage('Verify Environment') {
      // Check node deps
      assert sh(script: 'node --version', returnStdout: true).trim() == "v9.4.0": "expected node version 9.4.0"
      assert sh(script: 'yarn --version', returnStdout: true).trim() == "1.3.2": "expected yarn version 1.3.2"
    }

    stage('Fetch dependencies') {
      yarn 'install'
    }

    stage('Test') {
      yarn 'test --ci --testResultsProcessor="jest-junit"'
      junit "${config.baseDir}/junit.xml"
    }

  }

  if(config.stage == 'dist') {

    container('nextjs42-builder') {
      stage('Build artifacts') {
        yarn 'build'
      }

      stage('Remove dev dependencies') {
        yarn 'install --production --ignore-scripts --prefer-offline'
      }

      stage('Copy artifacts to staging area') {
        sh "mkdir -p ${artifactDir}"
        sh "cp -r \"${config.baseDir}/.next\" ${artifactDir}"
        sh "cp -r \"${config.baseDir}/node_modules\" ${artifactDir}"
      }
    }

    stage('Archive to Jenkins') {
      def tarName = "${config.project}-${config.component}-${config.buildNumber}.tar.gz"
      sh "tar -czvf \"${tarName}\" -C \"${artifactDir}\" ."
      archiveArtifacts tarName
    }

  }

}
