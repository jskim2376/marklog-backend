class PostCreate {


	drawImage(editor) {
		uploadImage(function(url) {
			imagemd = "![](" + url + ")";
			var cm = editor.codemirror;
			cm.replaceSelection(imagemd);
			cm.focus();
		})
	}

	uploadImage(imageHandler) {
		var input = document.createElement("input");
		input.type = "file";
		input.accept = "image/*";
		input.id = "uploadInput";
		input.onchange = function(event) {
			let xhr = new XMLHttpRequest();
			var formData = new FormData();
			xhr.open('POST', 'https://api.imgbb.com/1/upload');
			formData.append("key", 'ba349a478f0a62e1d11c353fe0ce35c1');
			formData.append("image", event.target.files[0]);
			xhr.onreadystatechange = function() {
				if (xhr.readyState === xhr.DONE) {
					if (xhr.status === 200) {
						res = JSON.parse(xhr.response);
						url = res.data.image.url;
						imageHandler(url);
					}
				}
			};
			xhr.send(formData);
		};
		input.click();
	}
}

let postCreate = new PostCreate();
postCreate.init();