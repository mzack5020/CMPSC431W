(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'Services', 'LoginService', '$state'];

    function HomeController ($scope, Principal, Services, LoginService, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.services = [];
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();
        getServices();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function getServices() {
            Services.query(function(result) {
                for(var i = 0; i < result.length; i++) {
                    if(result[i].reportedCount == 5) {
                        result.splice(i, 1);
                        console.log("HIT");
                    }
                    vm.services = result;
                }
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();
