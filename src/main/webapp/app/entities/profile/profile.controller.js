(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('ProfileController', ProfileController);

    ProfileController.$inject = ['$scope', '$state', 'Profile', 'AlertService', 'AccountHelp', 'Customer', 'Principal'];

    function ProfileController ($scope, $state, Profile, AlertService, AccountHelp, Customer, Principal) {
        var vm = this;

        vm.id = null;
        vm.email = null;
        vm.services = [];

        getAccount();

        function getAccount() {
            Principal.identity().then(function (user) {
                var data = {user: user};
                console.log(data.user.email);
                getAccountMapping(data.user.email);
            });
        }
        function getAccountMapping(userEmail) {
            AccountHelp.query({email : userEmail}, onSuccess, onError);
            function onSuccess(data, headers) {
                console.log(data);
                getServices(data.id);
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function getServices(userId) {
            Profile.query({id : userId}, onSuccess, onError);
            function onSuccess(data, headers) {
                vm.services = data;
                for(var i = 0; i < vm.services.length; i++) {
                    if (vm.services[i].completed == 0) {
                        vm.services[i].completed = "Not Completed";
                    } else {
                        vm.services[i].completed = "Completed";
                    }
                }
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
    }
})();
