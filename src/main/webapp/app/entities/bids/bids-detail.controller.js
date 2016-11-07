(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('BidsDetailController', BidsDetailController);

    BidsDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Bids', 'Services', 'Contractor'];

    function BidsDetailController($scope, $rootScope, $stateParams, previousState, entity, Bids, Services, Contractor) {
        var vm = this;

        vm.bids = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('eLancerApp:bidsUpdate', function(event, result) {
            vm.bids = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
