const uploadContainer = document.getElementById('upload-container');
const fileInput = document.getElementById('file-input');
const fileInfo = document.getElementById('file-info');
const fileNameSpan = document.getElementById('file-name');
const removeButton = document.getElementById('remove-button');
const submitButton = document.getElementById('submit-button');
const errorMessage = document.getElementById('error-message');

uploadContainer.addEventListener('click', () => {
	fileInput.click();
});

uploadContainer.addEventListener('dragover', (event) => {
	event.preventDefault();
	uploadContainer.classList.add('dragover');
});

uploadContainer.addEventListener('dragleave', () => {
	uploadContainer.classList.remove('dragover');
});

uploadContainer.addEventListener('drop', (event) => {
	event.preventDefault();
	uploadContainer.classList.remove('dragover');

	const file = event.dataTransfer.files[0];
	handleFile(file);
});

fileInput.addEventListener('change', (event) => {
	const file = event.target.files[0];
	handleFile(file);
});

removeButton.addEventListener('click', () => {
	fileInput.value = '';
	fileInfo.style.display = 'none';
	uploadContainer.querySelector('p').style.display = 'block';
	submitButton.style.display = 'none';
	errorMessage.textContent = '';
});

function handleFile(file) {
	if (file) {
		const validExtensions = ['image/png', 'image/jpeg', 'image/jpg', 'image/gif'];
		if (!validExtensions.includes(file.type)) {
			errorMessage.textContent = 'Invalid file type. Only images (PNG, JPG, GIF) are allowed.';
			return;
		}

		errorMessage.textContent = '';
		fileNameSpan.textContent = file.name;
		fileInfo.style.display = 'block';
		uploadContainer.querySelector('p').style.display = 'none';
		submitButton.style.display = 'block';
	}
}