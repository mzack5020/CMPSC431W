(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('SortedBidsController', SortedBidsController);

    SortedBidsController.$inject = ['$scope', '$state', 'entity', 'AlertService', 'Principal'];

    function SortedBidsController ($scope, $state, entity, AlertService, Principal) {
        var vm = this;
        vm.bids = entity;
        console.log(entity);
    }
})();
