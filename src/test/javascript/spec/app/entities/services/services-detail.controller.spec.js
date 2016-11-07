'use strict';

describe('Controller Tests', function() {

    describe('Services Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockServices, MockCustomer, MockCategories;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockServices = jasmine.createSpy('MockServices');
            MockCustomer = jasmine.createSpy('MockCustomer');
            MockCategories = jasmine.createSpy('MockCategories');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Services': MockServices,
                'Customer': MockCustomer,
                'Categories': MockCategories
            };
            createController = function() {
                $injector.get('$controller')("ServicesDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'eLancerApp:servicesUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
