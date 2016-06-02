angular.module('moneyManager.categories', [
    'ui.router',
    'placeholders',
    'ui.bootstrap',
    'ngMessages',
    'ngResource'
])

    .factory('categoryService', function ($resource) {

        var service = {};

        var Category = $resource("/categories/:id", {}, {
            update: {
                method: 'PUT'
            }
        });

        service.getAll = function (success) {
            return Category.query().$promise.then(function (response) {
                if (success) {
                    success(response);
                } else {
                    return response;
                }
            });
        };

        service.getCategory = function (id) {
            return Category.get({id: id}, function (data) {
                return data;
            });
        };

        service.addCategory = function (category, success, error) {
            Category.save(category, function (response) {
                if (success) {
                    success(response);
                }
            }, function (response) {
                if (error) {
                    error(response);
                }
            });
        };

        service.updateCategory = function (id, category, success, error) {
            Category.update({id: id}, category, function (response) {
                if (success) {
                    success(response);
                }
            }, function (response) {
                if (error) {
                    error(response);
                }
            });
        };

        service.deleteCategory = function (id, success, error) {
            Category.delete({id: id}, function (response) {
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
        $stateProvider.state('categories', {
            url: '/categories',
            views: {
                "main": {
                    controller: 'CategoriesCtrl',
                    templateUrl: 'categories/categories.tpl.html'
                }
            },
            resolve: {
                categories: function (categoryService) {
                    return categoryService.getAll();
                }
            },
            data: {pageTitle: 'Categories'}
        });
    })


    .controller('CategoriesCtrl', function CategoriesCtrl($scope, categoryService, categories, $filter, $state, $uibModal) {

        $scope.categories = categories;

        $scope.category = {};

        $scope.addCategory = function () {
            categoryService.addCategory($scope.category, function (data) {
                $scope.categories.push(data);
                $scope.category = {};
                $scope.categoryFormError = undefined;
                $scope.showForm = false;
                $scope.categoryForm.$setPristine();
            }, function (data) {
                $scope.categoryFormError = data.error;
            });
        };

        $scope.updateCategory = function (id) {
            categoryService.updateCategory(id, $scope.category, function (data) {
                $scope.category.id = id;

                for (var i in $scope.categories) {
                    var category = $scope.categories[i];
                    if (category.id === id) {
                        $scope.categories[i] = angular.copy($scope.category);
                    }
                }

                $scope.category = {};
                $scope.categoryFormError = undefined;
                $scope.categoryForm.$setPristine();
                $scope.showForm = false;
            }, function (data) {
                $scope.categoryFormError = data.error;
            });
        };


        $scope.editCategory = function (id) {
            $scope.showForm = true;
            if (id === 'new') {
                $scope.edit = false;
                $scope.category = {};
            } else {
                $scope.edit = true;
                $scope.category = angular.copy($filter('filter')($scope.categories, {id: id})[0]);
            }
        };

        $scope.removeCategory = function (id) {

            var ModalInstanceCtrl = function ($scope, $uibModalInstance) {

                $scope.ok = function () {
                    $uibModalInstance.close();
                };

                $scope.cancel = function () {
                    $uibModalInstance.dismiss('cancel');
                };
            };

            var modalInstance = $uibModal.open({
                templateUrl: 'categories/_remove.tpl.html',
                controller: ModalInstanceCtrl,
                size: 'sm'
            });

            modalInstance.result.then(function () {
                categoryService.deleteCategory(id, function (data) {
                    $scope.categoryFormError = undefined;
                    $scope.category = {};
                    $state.go($state.current, {}, {reload: true});
                }, function (data) {
                    $scope.categoryFormError = data.error;
                });
            });
        };

    })

;