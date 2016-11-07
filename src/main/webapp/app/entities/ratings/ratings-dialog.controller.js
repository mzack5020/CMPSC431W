(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('RatingsDialogController', RatingsDialogController);

    RatingsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Ratings', 'Customer', 'Contractor', 'Services'];

    function RatingsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Ratings, Customer, Contractor, Services) {
        var vm = this;

        vm.ratings = entity;
        vm.clear = clear;
        vm.save = save;
        vm.customers = Customer.query({filter: 'ratings-is-null'});
        $q.all([vm.ratings.$promise, vm.customers.$promise]).then(function() {
            if (!vm.ratings.customer || !vm.ratings.customer.id) {
                return $q.reject();
            }
            return Customer.get({id : vm.ratings.customer.id}).$promise;
        }).then(function(customer) {
            vm.customers.push(customer);
        });
        vm.contractors = Contractor.query({filter: 'ratings-is-null'});
        $q.all([vm.ratings.$promise, vm.contractors.$promise]).then(function() {
            if (!vm.ratings.contractor || !vm.ratings.contractor.id) {
                return $q.reject();
            }
            return Contractor.get({id : vm.ratings.contractor.id}).$promise;
        }).then(function(contractor) {
            vm.contractors.push(contractor);
        });
        vm.services = Services.query({filter: 'ratings-is-null'});
        $q.all([vm.ratings.$promise, vm.services.$promise]).then(function() {
            if (!vm.ratings.services || !vm.ratings.services.id) {
                return $q.reject();
            }
            return Services.get({id : vm.ratings.services.id}).$promise;
        }).then(function(services) {
            vm.services.push(services);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.ratings.id !== null) {
                Ratings.update(vm.ratings, onSaveSuccess, onSaveError);
            } else {
                Ratings.save(vm.ratings, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('eLancerApp:ratingsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
