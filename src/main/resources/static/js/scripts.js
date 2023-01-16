/*!
 * Start Bootstrap - Clean Blog v6.0.8 (https://startbootstrap.com/theme/clean-blog)
 * Copyright 2013-2022 Start Bootstrap
 * Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-clean-blog/blob/master/LICENSE)
 */
class Index {
	init() {
		if (!getCookie('json-token')) {
			document.getElementById('login-nav').className += document.getElementById('login-nav').className.replace(' d-none', '')
			document.getElementById('logout-nav').className += ' d-none';
		} else {
			document.getElementById('login-nav').className += ' d-none';
			document.getElementById('logout-nav').className += document.getElementById('logout-nav').className.replace(' d-none', '')
		}
	}
	getCookie(name) {
		var pattern = RegExp(name + "=.[^;]*")
		var matched = document.cookie.match(pattern)
		if (matched) {
			var cookie = matched[0].split('=')
			return cookie[1]
		}
		return false
	}
}

let index = new Index();
index.init();