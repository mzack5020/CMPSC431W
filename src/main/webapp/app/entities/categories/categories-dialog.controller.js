(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('CategoriesDialogController', CategoriesDialogController);

    CategoriesDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Categories'];

    function CategoriesDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Categories) {
        var vm = this;

        vm.categories = entity;
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
            if (vm.categories.id !== null) {
                Categories.update(vm.categories, onSaveSuccess, onSaveError);
            } else {
                Categories.save(vm.categories, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('eLancerApp:categoriesUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
