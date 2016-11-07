(function() {
    'use strict';

    angular
        .module('eLancerApp')
        .factory('ContractorSearch', ContractorSearch);

    ContractorSearch.$inject = ['$resource'];

    function ContractorSearch($resource) {
        var resourceUrl =  'api/_search/contractors/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
