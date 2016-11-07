(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('ServicesDialogController', ServicesDialogController);

    ServicesDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Services', 'Customer', 'Categories'];

    function ServicesDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Services, Customer, Categories) {
        var vm = this;

        vm.services = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.customers = Customer.query({filter: 'id-is-null'});
        $q.all([vm.services.$promise, vm.customers.$promise]).then(function() {
            if (!vm.services.customer || !vm.services.customer.id) {
                return $q.reject();
            }
            return Customer.get({id : vm.services.customer.id}).$promise;
        }).then(function(customer) {
            vm.customers.push(customer);
        });
        vm.categories = Categories.query({filter: 'services-is-null'});
        $q.all([vm.services.$promise, vm.categories.$promise]).then(function() {
            if (!vm.services.categories || !vm.services.categories.id) {
                return $q.reject();
            }
            return Categories.get({id : vm.services.categories.id}).$promise;
        }).then(function(categories) {
            vm.categories.push(categories);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.services.id !== null) {
                Services.update(vm.services, onSaveSuccess, onSaveError);
            } else {
                Services.save(vm.services, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('eLancerApp:servicesUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.datePosted = false;
        vm.datePickerOpenStatus.expirationDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
