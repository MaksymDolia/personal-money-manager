angular.module('moneyManager.accounts', [
    'ui.router',
    'placeholders',
    'ui.bootstrap',
    'ngResource'
])

    .factory('accountService', function ($resource, $http) {

        var service = {};

        var Account = $resource('/accounts/:id', {}, {
            update: {
                method: 'PUT'
            }
        });

        service.getAll = function (success) {
            return Account.query().$promise.then(function (response) {
                if (success) {
                    success(response);
                } else {
                    return response;
                }
            });
        };

        service.addAccount = function (account, success, error) {
            Account.save(account, function (response) {
                if (success) {
                    success(response);
                }
            }, function (response) {
                if (error) {
                    error(response);
                }
            });
        };

        service.getBalance = function () {
            return $http.get("/accounts/balance").then(function (response) {
                return response.data.balance;
            });
        };

        service.deleteAccount = function (id, success, error) {
            Account.delete({id: id}, function (response) {
                if (success) {
                    success(response);
                }
            }, function (response) {
                if (error) {
                    error(response);
                }
            });
        };

        return service;
    })

    .config(function config($stateProvider) {
        $stateProvider.state('accounts', {
            url: '/accounts',
            views: {
                "main": {
                    controller: 'AccountsCtrl',
                    templateUrl: 'accounts/accounts.tpl.html'
                }
            },
            resolve: {
                balance: function (accountService) {
                    return accountService.getBalance();
                },
                accounts: function (accountService) {
                    return accountService.getAll();
                }
            },
            data: {pageTitle: 'Accounts'}
        });
    })

    .controller('AccountsCtrl', function AccountsCtrl($scope, balance, accounts, $filter, accountService) {

        $scope.balance = balance;

        $scope.accounts = accounts;

        $scope.accountForm = {};

        $scope.editAccount = function (id) {
            $scope.showAccountForm = true;
            if (id === 'new') {
                $scope.accountForm = {};
            } else {
                $scope.accountForm = angular.copy($filter('filter')($scope.accounts, {id: id})[0]);
            }
        };

        $scope.setModalData = function (id, name) {
            $scope.modalData = {
                id: id,
                name: name
            };
        };

        $scope.deleteAccount = function (id) {
            accountService.deleteAccount(id, function (data) {
                $scope.accountFormError = undefined;
                $scope.accountForm = {};
                $state.go($state.current, {}, {reload: true});
            }, function (data) {
                $scope.categoryFormError = data.error;
            });
        };
    })

    .directive('accountForm', function () {
        return {
            templateUrl: 'accounts/_accountForm.tpl.html',
            controller: function ($scope, $attrs, accountService, $state) {

                $scope.modal = $attrs.modal ? true : false;

                $scope.accountForm = {};

                $scope.addAccount = function () {

                    accountService.addAccount($scope.accountForm, function (data) {
                        $scope.accountFormError = undefined;
                        $scope.accountForm = {};
                        angular.element("#addAccountModal").modal('hide');
                        $state.go($state.current, {}, {reload: true});
                    }, function (data) {
                        $scope.accountFormError = data.error;
                    });
                };

                $scope.removeAccount = function (id) {

                    accountService.deleteAccount(id, function (data) {

                    }, function (data) {

                    });
                };
            }
        };
    })

;