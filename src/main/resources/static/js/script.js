$(function() {
	var $userRegister = $("#userRegister");
	var $login = $("#login");
	var $resetPassword = $("#resetPassword");
	var $forgetPassword = $("#forgetPassword");
	var $order = $("#order")


	//  USER REGISTER VALIDATION	

	$userRegister.validate({

		rules: {
			name: {
				required: true,
				letteronly: true
			},
			email: {
				required: true,
				space: true,
				email: true
			},
			phone: {
				required: true,
				space: true,
				numericonly: true,
				minlength: 10,
				maxlength: 12
			},
			password: {
				required: true,
				space: true
			},
			cpassword: {
				required: true,
				space: true,
				equalTo: '#pass'
			},
			houseNo: {
				required: true,
				all: true
			},
			street: {
				required: true,
				all: true
			},
			place: {
				required: true,
				all: true
			},
			pincode: {
				required: true,
				numericonly: true
			},
			district: {
				required: true,
				letteronly: true
			},
			state: {
				required: true,
				letteronly: true
			},
			image: {
				required: true
			}
		},
		messages: {
			name: {
				required: 'name required',
				letteronly: 'invalid name'
			},
			email: {
				required: 'email must be required',
				space: 'space not allowed',
				email: 'Invalid email'
			},
			phone: {
				required: 'mobile number is required',
				space: 'Space not allowed',
				numericonly: 'Invalid mobile number',
				minlength: 'minimum 10 digits',
				maxlength: 'maximum 12 digits'
			},
			password: {
				required: 'password is required',
				space: 'space not allowed'
			},
			cpassword: {
				required: 'Confirm password is required',
				space: 'space not allowed',
				equalTo: 'password mismatch'
			},
			houseNo: {
				required: 'House/ Plat No is required',
				all: 'Invalid'
			},
			street: {
				required: 'Street/ area/ locality is required',
				all: 'Invalid'
			},
			place: {
				required: 'City/ Town/ Village is required',
				all: 'Invalid'
			},
			pincode: {
				required: 'pincode is required',
				numericonly: 'Invalid pincode'
			},
			district: {
				required: 'district is required',
				letteronly: 'Invalid district'
			},
			state: {
				required: 'state is required',
				letteronly: 'Invalid state'
			},
			image: {
				required: 'Image required'
			}
		}
	})

	//  LOGIN VALIDATION

	$login.validate({

		rules: {
			email: {
				required: true,
				emial: true
			},
			password: {
				required: true,
				space: true,
			}
		},

		messages: {
			email: {
				required: 'emial is required',
				emial: 'Invalid emial'
			},
			password: {
				required: 'password is required',
				space: 'space not allowed'
			}
		}
	})

	//  FORGET PASSWORD VALIDATION

	$forgetPassword.validate({
		rules: {
			email: {
				required: true,
				email: true
			}

		},

		messages: {
			email: {
				required: 'email must be required',
				email: 'Invalid email'
			}
		}

	})

	//  RESET PASSWORD VALIDATION

	$resetPassword.validate({

		rules: {
			password: {
				required: true,
				space: true
			},
			cpassword: {
				required: true,
				space: true,
				equalTo: '#pass'
			}
		},

		messages: {
			password: {
				required: 'password is required',
				space: 'space not allowed'
			},
			cpassword: {
				required: 'Confirm password is required',
				space: 'space not allowed',
				equalTo: 'Password doesnot match'
			}
		}
	})

	// ORDER VALIDATION

	$order.validate({

		rules: {
			firstName: {
				required: true,
				letteronly: true
			},
			lastName: {
				required: true,
				letteronly: true
			},
			email: {
				required: true,
				space: true,
				email: true
			},
			phone: {
				required: true,
				space: true,
				numericonly: true,
				minlength: 10,
				maxlength: 12
			},
			address: {
				required: true,
				all: true
			},
			pincode: {
				required: true,
				numericonly: true
			},
			district: {
				required: true,
				letteronly: true
			},
			state: {
				required: true,
				letteronly: true
			},
			image: {
				required: true
			},
			paymentType: {
				required: true
			}
		},
		messages: {
			firstName: {
				required: 'first name required',
				letteronly: 'invalid name'
			},
			lastName: {
				required: 'last name required',
				letteronly: 'invalid name'
			},
			email: {
				required: 'email must be required',
				space: 'space not allowed',
				email: 'Invalid email'
			},
			phone: {
				required: 'mobile number is required',
				space: 'Space not allowed',
				numericonly: 'Invalid mobile number',
				minlength: 'minimum 10 digits',
				maxlength: 'maximum 12 digits'
			},
			address: {
				required: 'address is required',
				all: 'Invalid'
			},
			pincode: {
				required: 'pincode is required',
				numericonly: 'Invalid pincode'
			},
			district: {
				required: 'district is required',
				letteronly: 'Invalid district'
			},
			state: {
				required: 'state is required',
				letteronly: 'Invalid state'
			},
			image: {
				required: 'Image required'
			},
			paymentType: {
				required: 'select payment type'
			}
		}
	})


})

jQuery.validator.addMethod('letteronly', function(value, element) {
	return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
});
jQuery.validator.addMethod('space', function(value, element) {
	return /^[^-\s]+$/.test(value);
});
jQuery.validator.addMethod('all', function(value, element) {
	return /^[^-\s][a-zA-Z0-9_,.-/\s-]+$/.test(value);
});
jQuery.validator.addMethod('numericonly', function(value, element) {
	return /^[0-9]+$/.test(value);
});

function loading() {
	setTimeout(() => {
		document.getElementById("loading-container").style.display = "none";
		document.getElementById("content").style.display = "block";
	}, 2000); // Delay to ensure smooth transition (adjust as needed)
}