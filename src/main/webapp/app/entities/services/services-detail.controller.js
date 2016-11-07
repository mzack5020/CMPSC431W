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
        configureBoolean();

        function configureBoolean() {
            if (vm.services.completed == 0) {
                vm.services.completed = "Not Completed";
            } else {
                vm.services.completed = "Completed";
            }
        }

        var unsubscribe = $rootScope.$on('eLancerApp:servicesUpdate', function(event, result) {
            vm.services = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
