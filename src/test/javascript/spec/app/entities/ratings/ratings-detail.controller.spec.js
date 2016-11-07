'use strict';

describe('Controller Tests', function() {

    describe('Ratings Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockRatings, MockCustomer, MockContractor, MockServices;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockRatings = jasmine.createSpy('MockRatings');
            MockCustomer = jasmine.createSpy('MockCustomer');
            MockContractor = jasmine.createSpy('MockContractor');
            MockServices = jasmine.createSpy('MockServices');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Ratings': MockRatings,
                'Customer': MockCustomer,
                'Contractor': MockContractor,
                'Services': MockServices
            };
            createController = function() {
                $injector.get('$controller')("RatingsDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'eLancerApp:ratingsUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
