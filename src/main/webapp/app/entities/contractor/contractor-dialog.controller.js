(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('ContractorDialogController', ContractorDialogController);

    ContractorDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Contractor'];

    function ContractorDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Contractor) {
        var vm = this;

        vm.contractor = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.contractor.id !== null) {
                Contractor.update(vm.contractor, onSaveSuccess, onSaveError);
            } else {
                Contractor.save(vm.contractor, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('eLancerApp:contractorUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
