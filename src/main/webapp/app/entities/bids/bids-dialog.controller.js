(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('BidsDialogController', BidsDialogController);

    BidsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Bids', 'Services', 'Contractor'];

    function BidsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Bids, Services, Contractor) {
        var vm = this;

        vm.bids = entity;
        vm.clear = clear;
        vm.save = save;
        vm.services = Services.query({filter: 'bids-is-null'});
        $q.all([vm.bids.$promise, vm.services.$promise]).then(function() {
            if (!vm.bids.services || !vm.bids.services.id) {
                return $q.reject();
            }
            return Services.get({id : vm.bids.services.id}).$promise;
        }).then(function(services) {
            vm.services.push(services);
        });
        vm.contractors = Contractor.query({filter: 'bids-is-null'});
        $q.all([vm.bids.$promise, vm.contractors.$promise]).then(function() {
            if (!vm.bids.contractor || !vm.bids.contractor.id) {
                return $q.reject();
            }
            return Contractor.get({id : vm.bids.contractor.id}).$promise;
        }).then(function(contractor) {
            vm.contractors.push(contractor);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.bids.id !== null) {
                Bids.update(vm.bids, onSaveSuccess, onSaveError);
            } else {
                Bids.save(vm.bids, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('eLancerApp:bidsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
