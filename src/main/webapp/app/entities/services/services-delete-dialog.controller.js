(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('ServicesDeleteController',ServicesDeleteController);

    ServicesDeleteController.$inject = ['$uibModalInstance', 'entity', 'Services'];

    function ServicesDeleteController($uibModalInstance, entity, Services) {
        var vm = this;

        vm.services = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Services.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
