(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('ProfileDetailController', ProfileDetailController);

    ProfileDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Services', 'Customer', 'Categories'];

    function ProfileDetailController($scope, $rootScope, $stateParams, previousState, entity, Services, Customer, Categories) {
        var vm = this;

        vm.services = entity;
        vm.previousState = previousState.name;
        vm.completed = completed;

        configureBoolean();
        configureButton();

        // TODO : Change button color and disabled

        function completed() {
            vm.services.completed = true;
            Services.update(vm.services, onSuccess, onError);

            function onSuccess(data, headers) {
                vm.services.completed = "Completed";
                var button = document.getElementById("completedButton");
                button.className = "btn btn-success";
                button.innerHTML = "<span class='glyphicon glyphicon-ok'></span><span class='hidden-xs'><strong>&nbsp;Completed</strong></span>";
                button.disabled = true;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function configureBoolean() {
            if (vm.services.completed == 0) {
                vm.services.completed = "Not Completed";
            } else {
                vm.services.completed = "Completed";
            }
        }

        function configureButton() {
            if(vm.services.completed == "Completed" || vm.services.completed == 1) {
                var button = document.getElementById("completedButton");
                button.className = "btn btn-success";
                button.innerHTML = "<span class='glyphicon glyphicon-ok'></span><span class='hidden-xs'><strong>&nbsp;Completed</strong></span>";
                button.disabled = true;
            }
        }

        var unsubscribe = $rootScope.$on('eLancerApp:servicesUpdate', function(event, result) {
            vm.services = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
