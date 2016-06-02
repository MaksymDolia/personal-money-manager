angular.module('moneyManager', [
    'templates-app',
    'templates-common',
    'directives',
    'moneyManager.dashboard',
    'moneyManager.transactions',
    'moneyManager.accounts',
    'moneyManager.categories',
    'ui.bootstrap',
    'ui.router'
])

    .config(function myAppConfig($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/dashboard');
    })

    .run(function run() {
    })

    .controller('AppCtrl', function AppCtrl($scope, $location, $http) {
        $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
            if (angular.isDefined(toState.data.pageTitle)) {
                $scope.pageTitle = toState.data.pageTitle + ' | PMM';
            }
        });

        $scope.logout = function () {

            $http.get("/logout").then(function (response) {
                window.location.href = '/';
            }, function (response) {
                window.location.href = '/';
            });
        };
    })

;

