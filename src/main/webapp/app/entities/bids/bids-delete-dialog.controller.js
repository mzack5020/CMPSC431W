(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('BidsDeleteController',BidsDeleteController);

    BidsDeleteController.$inject = ['$uibModalInstance', 'entity', 'Bids'];

    function BidsDeleteController($uibModalInstance, entity, Bids) {
        var vm = this;

        vm.bids = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Bids.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
