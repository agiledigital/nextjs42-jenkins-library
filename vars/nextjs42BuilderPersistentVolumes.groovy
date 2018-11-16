def call(Map config) {
  return [
    [
      path: '/home/jenkins/.npm',
      claimName: "${config.project}-home-jenkins-npm",
      sizeGiB: 1
    ],
    [
      path: '/home/jenkins/.cache',
      claimName: "${config.project}-home-jenkins-cache",
      sizeGiB: 2
    ]
  ]
}
