(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .factory('ServicesSearch', ServicesSearch);

    ServicesSearch.$inject = ['$resource'];

    function ServicesSearch($resource) {
        var resourceUrl =  'api/_search/services/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
