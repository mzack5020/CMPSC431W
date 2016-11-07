(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('ServicesDetailController', ServicesDetailController);

    ServicesDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Services', 'Customer', 'Categories'];

    function ServicesDetailController($scope, $rootScope, $stateParams, previousState, entity, Services, Customer, Categories) {
        var vm = this;

        vm.services = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('eLancerApp:servicesUpdate', function(event, result) {
            vm.services = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
