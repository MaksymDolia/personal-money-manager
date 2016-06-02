angular.module('moneyManager.web', [
    'ngResource'
])

    .factory('userService', function ($resource, $http) {

        var service = {};

        var User = $resource('/users/:id', {}, {
            update: {
                method: 'PUT'
            }
        });

        return service;
    })

    .factory('authService', ['$http', function ($http) {

        var service = {};

        service.login = function (credentials, success, error) {

            var data = "username=" + credentials.email + "&password=" + credentials.password;

            $http.post("/login", data, {headers: {'Content-Type': 'application/x-www-form-urlencoded'}})
                .then(function (response) {
                    success && success();
                }, function (response) {
                    error && error();
                });
        };

        return service;
    }])

    .controller('UserCtrl', ['$scope', 'userService', 'authService', function ($scope, userService, authService) {

        $scope.credentials = {};

        $scope.doLogin = function () {

            authService.login($scope.credentials, function () {
                $scope.loginError = undefined;
                window.location.href = '/app';
            }, function () {
                $scope.loginError = true;
            });

        };

    }])

;