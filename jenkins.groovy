pipeline {
    agent any

    parameters {
        choice(
            name: 'JOB_TO_CREATE',
            choices: [
                'createdbyseedjob',
                'TFJOB-createdbyseedjob'
                'TFJOB-from-shared-library'
            ],
            description: 'Select which job should be created'
        )
    }

    stages {

        stage('Create Job') {
            when {
                expression {
                    params.JOB_TO_CREATE == 'createdbyseedjob'
                }
            }

            steps {
                jobDsl(
                    scriptText: '''
                        pipelineJob('createdbyseedjob') {
                            description('Pipeline created by Seed Job')

                            definition {
                                cps {
                                    script("""
                                        pipeline {
                                            agent any

                                            stages {
                                                stage('Hello') {
                                                    steps {
                                                        echo 'Hello from the generated pipeline!'
                                                    }
                                                }
                                            }
                                        }
                                    """.stripIndent())

                                    sandbox()
                                }
                            }
                        }
                    '''
                )
            }
        }

        stage('Create TF Job') {
            when {
                expression {
                    params.JOB_TO_CREATE == 'TFJOB-createdbyseedjob'
                }
            }

            steps {
                jobDsl(
                    scriptText: '''
                        pipelineJob('TFJOB-createdbyseedjob') {
                            description('TF Pipeline created by Seed Job')

                            definition {
                                cpsScm {
                                    scm {
                                        git {
                                            remote {
                                                url('https://github.com/anandambitionv2/jenkinspipeline.git')
                                            }

                                            branch('*/main')
                                        }
                                    }

                                    scriptPath('JenkinsFile')
                                    lightweight()
                                }
                            }
                        }
                    '''
                )
            }
        }
        stage('TF Job from shared-library') {
            when {
                expression {
                    params.JOB_TO_CREATE == 'TFJOB-from-shared-library'
                }
            }
           
            steps {
                jobDsl(
                    scriptText: '''
                        pipelineJob('TFJOB-from-shared-library') {
                            description('TF Pipeline created by Seed Job')

                            definition {
                                cpsScm {
                                    scm {
                                        git {
                                            remote {
                                                url('https://github.com/anandambitionv2/jenkinsapppipeline.git')
                                            }

                                            branch('*/main')
                                        }
                                    }

                                    scriptPath('apptf.groovy')
                                    lightweight()
                                }
                            }
                        }
                    '''
                )
            }
        }

    }
}