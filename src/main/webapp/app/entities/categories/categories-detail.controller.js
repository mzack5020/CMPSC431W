(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('CategoriesDetailController', CategoriesDetailController);

    CategoriesDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Categories'];

    function CategoriesDetailController($scope, $rootScope, $stateParams, previousState, entity, Categories) {
        var vm = this;

        vm.categories = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('eLancerApp:categoriesUpdate', function(event, result) {
            vm.categories = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
