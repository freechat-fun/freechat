import { forwardRef } from 'react';
import { Box, BoxProps, Card, Skeleton, Stack } from '@mui/material';
import StyledStack from './StyledStack';

type DocumentSkeletonProps = BoxProps & {
  lines?: number;
  loading?: boolean;
};

const DocumentSkeleton = forwardRef<HTMLDivElement, DocumentSkeletonProps>(
  (props, ref) => {
    const { lines = 1, loading = true, sx, ...others } = props;

    const titles = Array.from({ length: lines }, (_, index) => (
      <StyledStack key={`title-${index}`} sx={{ p: 1 }}>
        <Skeleton />
      </StyledStack>
    ));

    return (
      <Box
        ref={ref}
        sx={{
          display: loading ? 'grid' : 'none',
          gridTemplateColumns: '1fr 3fr',
          alignItems: 'stretch',
          gap: 2,
          pb: 4,
          pr: 4,
          ...sx,
        }}
        {...others}
      >
        <Stack spacing={1}>{titles}</Stack>
        <Card variant="outlined" sx={{ p: 1 }}>
          <Skeleton />
        </Card>
      </Box>
    );
  }
);

export default DocumentSkeleton;
