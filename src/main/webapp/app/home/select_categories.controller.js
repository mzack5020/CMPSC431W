(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('SelectCategoryController', SelectCategoryController);

    SelectCategoryController.$inject = ['$scope', 'Principal', 'CategoriesAll', '$state', 'AlertService'];

    function SelectCategoryController ($scope, Principal, CategoriesAll, $state, AlertService) {
        var vm = this;

        vm.categories = null;
        getCategories();

        function getCategories() {
            CategoriesAll.query({}, onSuccess, onError);

            function onSuccess(data, headers) {
                console.log(data);
                vm.categories = data;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
    }
})();
