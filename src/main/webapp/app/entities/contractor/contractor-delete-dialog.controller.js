(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('ContractorDeleteController',ContractorDeleteController);

    ContractorDeleteController.$inject = ['$uibModalInstance', 'entity', 'Contractor'];

    function ContractorDeleteController($uibModalInstance, entity, Contractor) {
        var vm = this;

        vm.contractor = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Contractor.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
