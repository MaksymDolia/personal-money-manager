// Functions for handling the transaction form
function showExpenseFormTab() {

	$("#transaction_box").css("background-color", "#F4AA6D");

	$("label#account").html("From account");

	$("#2-expense").removeClass("hidden").addClass("show");
	$("#2-transfer").removeClass("show").addClass("hidden");
	$("#2-transfer select#transferAccount").attr("disabled", true);
	$("#2-expense input#date").attr("disabled", false);
	$("#2-expense select#category").attr("disabled", false);
	$("#2-income input#date").attr("disabled", true);
	$("#2-income select#category").attr("disabled", true);
	$("#2-income").removeClass("show").addClass("hidden");
	$("#3 label#date").removeClass("show").addClass("hidden");
	$("#3 div#date").removeClass("show").addClass("hidden");
	$("#3 input#date").attr("disabled", true);
}

function showTransferFormTab() {
	$("#transaction_box").css("background-color", "#B4BEC6");

	$("label#account").html("From account");

	$("#2-expense").removeClass("show").addClass("hidden");
	$("#2-transfer").removeClass("hidden").addClass("show");
	$("#2-transfer select#transferAccount").attr("disabled", false);
	$("#2-expense input#date").attr("disabled", true);
	$("#2-expense select#category").attr("disabled", true);
	$("#2-income").removeClass("show").addClass("hidden");
	$("#2-income input#date").attr("disabled", true);
	$("#2-income select#category").attr("disabled", true);
	$("#3 label#date").removeClass("hidden").addClass("show");
	$("#3 div#date").removeClass("hidden").addClass("show");
	$("#3 input#date").attr("disabled", false);
}

function showIncomeFormTab() {
	$("#transaction_box").css("background-color", "#33D633");

	$("label#account").html("To account");

	$("#2-expense").removeClass("show").addClass("hidden");
	$("#2-transfer").removeClass("show").addClass("hidden");
	$("#2-transfer select#transferAccount").attr("disabled", true);
	$("#2-expense input#date").attr("disabled", true);
	$("#2-expense select#category").attr("disabled", true);
	$("#2-income").removeClass("hidden").addClass("show");
	$("#2-income input#date").attr("disabled", false);
	$("#2-income select#category").attr("disabled", false);
	$("#3 label#date").removeClass("show").addClass("hidden");
	$("#3 div#date").removeClass("show").addClass("hidden");
	$("#3 input#date").attr("disabled", true);
}

// VALIDATION FORMS

// function for validation of registration form
function validateRegistrationForm() {
	$(".registration-form").validate(
			{
				rules : {
					email : {
						required : true,
						email : true,
						maxlength : 255,
						remote : {
							url : "signin/available_email",
							type : "get"
						}
					},
					password : {
						required : true,
						minlength : 5,
						maxlength : 255
					},
					password_again : {
						required : true,
						minlength : 5,
						maxlength : 255,
						equalTo : "#password"
					}
				},
				highlight : function(element) {
					$(element).closest(".form-group")
							.removeClass("has-success").addClass("has-error");
				},
				unhighlight : function(element) {
					$(element).closest(".form-group").removeClass("has-error")
							.addClass("has-success");
				},
				messages : {
					email : {
						remote : "This email already exists"
					}
				}
			});
};

// function for validation for form-category-add
function validateCategoryAddForm() {
	$(".form-category-add").validate(
			{
				rules : {
					name : {
						required : true,
						minlength : 1,
						maxlength : 255
					},
					operation : {
						required : true,
					}
				},
				highlight : function(element) {
					$(element).closest(".form-group")
							.removeClass("has-success").addClass("has-error");
				},
				unhighlight : function(element) {
					$(element).closest(".form-group").removeClass("has-error")
							.addClass("has-success");
				}
			});
}

// edit category form validation
function validateCategoryEditForm() {
	$(".form-category-edit").validate(
			{
				rules : {
					name : {
						required : true,
						minlength : 1,
						maxlength : 255
					},
					operation : {
						required : true,
					}
				},
				highlight : function(element) {
					$(element).closest(".form-group")
							.removeClass("has-success").addClass("has-error");
				},
				unhighlight : function(element) {
					$(element).closest(".form-group").removeClass("has-error")
							.addClass("has-success");
				}
			});
}

// add account form validation
function validateAccountAddForm() {
	$(".form-account-add").validate(
			{
				rules : {
					name : {
						required : true,
						minlength : 1,
						maxlength : 255
					},
					amount : {
						required : true,
						number : true
					}
				},
				highlight : function(element) {
					$(element).closest(".form-group")
							.removeClass("has-success").addClass("has-error");
				},
				unhighlight : function(element) {
					$(element).closest(".form-group").removeClass("has-error")
							.addClass("has-success");
				}
			});
}

// edit account form validation
function validateAccountEditForm() {
	$(".form-account-edit").validate(
			{
				rules : {
					name : {
						required : true,
						minlength : 1,
						maxlength : 255
					},
					amount : {
						required : true,
						number : true
					}
				},
				highlight : function(element) {
					$(element).closest(".form-group")
							.removeClass("has-success").addClass("has-error");
				},
				unhighlight : function(element) {
					$(element).closest(".form-group").removeClass("has-error")
							.addClass("has-success");
				}
			});
}

// add transaction form validation
function validateTransactionForm() {
	$("#form-transaction").validate(
			{
				rules : {
					account : {
						required : true
					},
					currency : {
						required : true
					},
					amount : {
						required : true,
						number : true
					},
					fromCurrency : {
						required : true
					},
					category : {
						required : true
					},
					date : {
						required : true,
						date : true
					},
					comment : {
						maxlength : 255
					},
					transferAccount : {
						required : true
					},
					transferCurrency : {
						required : true
					}
				},
				highlight : function(element) {
					$(element).closest(".form-group")
							.removeClass("has-success").addClass("has-error");
				},
				unhighlight : function(element) {
					$(element).closest(".form-group").removeClass("has-error")
							.addClass("has-success");
				}
			});
}

// function-handler for modals for removing elements
function handleRemoveModal() {
	$(".trigger-remove").click(function(e) {
		e.preventDefault();
		$("#btn-remove").attr("href", $(this).closest(".trigger-remove").attr("href"));
		$("#remove-modal").modal();
	})
}

// function for handling transaction form
function handleTransactionForm() {
	$("#operation-expense").click(function() {
		showExpenseFormTab()
	});
	$("#operation-transfer").click(function() {
		showTransferFormTab()
	});
	$("#operation-income").click(function() {
		showIncomeFormTab()
	});
}

$(document).ready(function() {
	handleRemoveModal();
	handleTransactionForm();

	validateRegistrationForm();
	validateCategoryAddForm();
	validateCategoryEditForm();
	validateAccountAddForm();
	validateAccountEditForm();
	validateTransactionForm();
})
