angular.module('moneyManager.transactions', [
    'ui.router',
    'placeholders',
    'ui.bootstrap',
    'ngResource'
])

    .factory('transactionService', function ($resource) {

        var service = {};

        var Transaction = $resource('/transactions/:id', {}, {
            update: {
                method: 'PUT'
            }
        });

        service.getAll = function () {
            return Transaction.query().$promise.then(function (response) {
                return response;
            });
        };

        service.addTransaction = function (transaction, success, error) {

            Transaction.save(transaction, function (response) {
                if (success) {
                    success(response);
                }
            }, function (response) {
                if (error) {
                    error(response);
                }
            });
        };

        service.delete = function (id, success, error) {
            Transaction.delete({id: id}, function (response) {
                if (success) {
                    success(response);
                }
            }, function (response) {
                if (error) {
                    error(response);
                }
            })
        };

        return service;
    })

    .config(function config($stateProvider) {
        $stateProvider.state('transactions', {
            url: '/transactions',
            views: {
                "main": {
                    controller: 'TransactionsCtrl',
                    templateUrl: 'transactions/transactions.tpl.html'
                }
            },
            resolve: {
                transactions: function (transactionService) {
                    return transactionService.getAll();
                },
                accounts: function (accountService) {
                    return accountService.getAll();
                },
                categories: function (categoryService) {
                    return categoryService.getAll();
                }
            },
            data: {pageTitle: 'Transactions'}
        });
    })

    .controller('TransactionsCtrl', function TransactionsCtrl($scope, transactions, $filter, transactionService, $state, accounts) {
        $scope.accounts = accounts;
        $scope.transactions = transactions;

        var date = new Date();
        var firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
        var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);

        $scope.showForm = false;

        $scope.searchForm = {
            fromDate: firstDay,
            toDate: lastDay
        };

        $scope.editTransaction = function (id) {
            $scope.showForm = true;
            if (id === 'new') {
                $scope.transactionForm = {};
            } else {
                $scope.transactionForm = angular.copy($filter('filter')($scope.transactions, {id: id})[0]);
            }
        };

        $scope.deleteTransaction = function (id) {
            transactionService.delete(id, function (data) {
                $scope.transactionFormError = undefined;
                $scope.transactionForm = {};
                angular.element("#removeTransactionModal").modal('hide');
                $state.go($state.current, {}, {reload: true});
            }, function (data) {
                $scope.transactionFormError = data.error;
            });
        };

        $scope.setModalData = function (id) {
            $scope.modalData = {
                id: id
            };
        };
    })

    .directive('transactionForm', function () {
        return {
            restrict: 'A',
            templateUrl: 'transactions/_transactionForm.tpl.html',
            controller: function ($scope, transactionService) {

                $scope.transactionForm = {};

                $scope.transactionForm.operation = 'expense';

                $scope.addTransaction = function () {

                    transactionService.addTransaction($scope.transactionForm, function (response) {
                        $scope.transactionForm = {};
                    }, function (response) {
                        $scope.transactionFormError = response.error;
                    });
                };

                // Datepicker
                $scope.datepicker = {
                    opened1: false,
                    opened2: false
                };

                $scope.datepicker.today = function () {
                    $scope.transactionForm.date = new Date();
                };
                $scope.datepicker.today();

                $scope.datepicker.clear = function () {
                    $scope.transactionForm.date = null;
                };

                $scope.datepicker.open1 = function () {
                    $scope.datepicker.opened1 = true;
                };

                $scope.datepicker.open2 = function () {
                    $scope.datepicker.opened2 = true;
                };

                $scope.setDate = function (year, month, day) {
                    $scope.transactionForm.date = new Date(year, month, day);
                };

                $scope.dateOptions = {
                    formatYear: 'yy',
                    startingDay: 1
                };

                $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'shortDate'];
                $scope.format = $scope.formats[0];
                $scope.altInputFormats = ['M!/d!/yyyy'];
            },
            scope: {
                isModal: '=',
                accounts: '=',
                categories: '='
            }
        };
    })

;