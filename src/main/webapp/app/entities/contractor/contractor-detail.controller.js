(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('ContractorDetailController', ContractorDetailController);

    ContractorDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Contractor'];

    function ContractorDetailController($scope, $rootScope, $stateParams, previousState, entity, Contractor) {
        var vm = this;

        vm.contractor = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('eLancerApp:contractorUpdate', function(event, result) {
            vm.contractor = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
