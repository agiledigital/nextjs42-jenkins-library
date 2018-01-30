def call() {
	return [
		containerTemplate(
			name: 'nextjs42-builder',
			image: 'agiledigital/nextjs42-builder',
	        alwaysPullImage: true,
			command: 'cat',
			ttyEnabled: true
		)
	]
}