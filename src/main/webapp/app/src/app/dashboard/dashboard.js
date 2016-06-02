angular.module('moneyManager.dashboard', [
    'ui.router',
    'placeholders',
    'ui.bootstrap',
    'ngResource',
    'moneyManager.accounts'
])

    .factory('DashboardService', function ($resource) {

        var service = {};

        return service;
    })

    .config(function config($stateProvider) {
        $stateProvider.state('dashboard', {
            url: '/dashboard',
            views: {
                "main": {
                    controller: 'DashboardCtrl',
                    templateUrl: 'dashboard/dashboard.tpl.html'
                }
            },
            resolve: {
                balance: function (accountService) {
                    return accountService.getBalance();
                },
                accounts: function (accountService) {
                    return accountService.getAll();
                },
                categories: function (categoryService) {
                    return categoryService.getAll();
                }
            },
            data: {pageTitle: 'Dashboard'}
        });
    })

    .controller('DashboardCtrl', function DashboardCtrl($scope, balance, accounts, categories, $uibModal, accountService) {

        $scope.balance = balance;
        $scope.accounts = accounts;
        $scope.categories = categories;

        $scope.addAccount = function () {

            var ModalInstanceCtrl = function ($scope, $uibModalInstance) {

                $scope.account = {};

                $scope.ok = function () {
                    $uibModalInstance.close($scope.account);
                };

                $scope.cancel = function () {
                    $uibModalInstance.dismiss('cancel');
                };
            };

            var modalInstance = $uibModal.open({
                templateUrl: 'dashboard/_addAccountModal.tpl.html',
                controller: ModalInstanceCtrl,
                size: 'sm'
            });

            modalInstance.result.then(function (account) {
                accountService.addAccount(account, function () {
                }, function () {
                });
            });
        }

    })

;