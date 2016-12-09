(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('ProfileNewRatingController', ProfileNewRatingController);

    ProfileNewRatingController.$inject = ['$scope', '$rootScope', '$q', '$stateParams', '$uibModalInstance', 'entity', 'Ratings', 'serviceId', 'Services', 'Contractor'];

    function ProfileNewRatingController($scope, $rootScope, $q, $stateParams, $uibModalInstance, entity, Ratings, serviceId, Services, Contractor) {
        var vm = this;

        vm.ratings = entity;
        vm.serviceId = serviceId.id;
        vm.service = null;
        vm.clear = clear;
        vm.save = save;

        getService();

        vm.contractors = Contractor.query({filter: 'ratings-is-null'});
        $q.all([vm.ratings.$promise, vm.contractors.$promise]).then(function() {
            if (!vm.ratings.contractor || !vm.ratings.contractor.id) {
                return $q.reject();
            }
            return Contractor.get({id : vm.ratings.contractor.id}).$promise;
        }).then(function(contractor) {
            vm.contractors.push(contractor);
        });

        function getAccount() {
            Principal.identity().then(function (user) {
                var data = {user: user};
                getAccountMapping(data.user.email);
            });
        }
        function getAccountMapping(userEmail) {
            AccountHelp.query({email : userEmail}, onSuccess, onError);
            function onSuccess(data, headers) {
                vm.user = data;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function getService() {
            Services.get({id: vm.serviceId}, onSuccess, onError);

            function onSuccess(data, headers) {
                vm.service = data;
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;

            vm.ratings.services = vm.service;
            vm.ratings.customer = vm.service.customer;

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
