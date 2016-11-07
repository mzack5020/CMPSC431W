(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('RatingsDeleteController',RatingsDeleteController);

    RatingsDeleteController.$inject = ['$uibModalInstance', 'entity', 'Ratings'];

    function RatingsDeleteController($uibModalInstance, entity, Ratings) {
        var vm = this;

        vm.ratings = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Ratings.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
