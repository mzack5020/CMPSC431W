(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('SortedServicesController', SortedServicesController);

    SortedServicesController.$inject = ['$scope', '$state', 'entity', 'AlertService', 'Principal'];

    function SortedServicesController ($scope, $state, entity, AlertService, Principal) {
        var vm = this;
        vm.services = entity;

        vm.noServices = document.getElementById("noServices");
        vm.servicesTable = document.getElementById("servicesTable");

        checkServicesLength();

        function checkServicesLength() {
            if(vm.services.length > 0) {
                vm.noServices.style.display = "none";
                vm.servicesTable.style.display = "inline";
                console.log("services found");
            } else {
                vm.noServices.style.display = "inline";
                vm.servicesTable.style.display = "none";
                console.log("no services found");
            }
        }
    }
})();
